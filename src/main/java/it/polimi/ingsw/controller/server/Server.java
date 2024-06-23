package it.polimi.ingsw.controller.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fusesource.jansi.AnsiConsole;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.Status;
import it.polimi.ingsw.distributed.events.main.CreateLobbyError;
import it.polimi.ingsw.distributed.events.main.JoinLobbyError;
import it.polimi.ingsw.distributed.events.main.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.main.LeaveLobbyError;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.LoginEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.events.main.ReconnectToGameEvent;
import it.polimi.ingsw.distributed.events.main.StartGameError;
import it.polimi.ingsw.distributed.server.rmi.RMIGameServer;
import it.polimi.ingsw.distributed.server.rmi.RMIHandler;
import it.polimi.ingsw.distributed.server.rmi.RMIMainServer;
import it.polimi.ingsw.distributed.server.socket.SocketClientHandler;
import it.polimi.ingsw.distributed.server.socket.SocketServer;

/**
 * The server is implemented using the Singleton pattern. It handles users,
 * lobbies and starting
 * games.
 */
public enum Server {
    INSTANCE;

    /**
     * Links the UserInfo to their respective client.
     * From the Client instance, the server can get their status.
     */
    private final ConcurrentHashMap<UserInfo, Client> connectedPlayers = new ConcurrentHashMap<>();

    /**
     * ExecutorService for all Runnables created by the server.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();;

    /**
     * Server entrypoint, must be called to start the Codex Naturalis server.
     * 
     * @param args not needed, they are ignored
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        try {
            new SocketServer(Config.MainSocketPort);
            ServerPrinter.displayInfo("Socket server started");
        } catch (IOException e) {
            ServerPrinter.displayError("Couldn't start Socket server");
            AnsiConsole.systemUninstall();
            System.exit(0);
        }

        try {
            new RMIMainServer().run();
            ServerPrinter.displayInfo("RMI server started");
        } catch (IOException e) {
            ServerPrinter.displayError("Couldn't start RMI server");
            AnsiConsole.systemUninstall();
            System.exit(0);
        }
    }

    /**
     * Constructor of the only instance of the Server singleton.
     */
    Server() {
        executorService.submit(() -> {
            while (true) {
                checkConnections();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    ServerPrinter.displayError("Check connection thread was interrupted.");
                }
            }
        });
    }

    /**
     * Function that checks if clients are connected and updates their status
     * otherwise.
     */
    public void checkConnections() {
        connectedPlayers.entrySet().stream()
                .filter(entry -> {
                    return entry.getValue().getStatus() == Status.IN_GAME ||
                            entry.getValue().getStatus() == Status.IN_MENU;
                })
                .forEach(
                        entry -> {
                            try {
                                entry.getValue().trasmitEvent(new KeepAliveEvent());
                                ServerPrinter.displayDebug("Sent keep alive to " + entry.getKey());
                            } catch (IOException e) {
                                if (entry.getValue().getStatus() == Status.IN_GAME) {
                                    entry.getValue().setStatus(Status.DISCONNETED_FROM_GAME);
                                } else if (entry.getValue().getStatus() == Status.IN_MENU)
                                    entry.getValue().setStatus(Status.OFFLINE);

                                ServerPrinter
                                        .displayError("Couldn't send connection check event to " + entry.getValue());
                                ServerPrinter.displayError("Client " + entry.getKey() + " disconnected");
                            }
                        });
    }

    /**
     * Adds the user to the lobby with the given id.
     *
     * @param userInfo The userInfo of the user who wants to join the lobby
     * @param lobbyId  The lobby id
     * @return True if joining was successfull, false if the lobby doesn't exist or
     *         if the user is
     *         already in the lobby.
     */
    public boolean joinLobby(UserInfo userInfo, int lobbyId) {
        Lobby lobby = Lobby.getLobby(lobbyId);

        // Try to add the user to the lobby
        String error = null;
        if (lobby == null)
            error = "The lobby " + lobbyId + " doesn't exist";
        else if (lobby.contains(userInfo))
            error = "You are already in the lobby " + lobbyId;
        else if (!lobby.addUser(User.userInfoToUser(userInfo))) {
            if (lobby.isFull()) {
                error = "The lobby " + lobbyId + " is full";
            } else {
                error = "You are already in another lobby";
            }
        }

        // If there was an error, send an error event to the client
        if (error != null) {
            sendMainEvent(userInfo, new JoinLobbyError(error));
            return false;
        }

        broadcastLobbies();
        return true;
    }

    /**
     * Removes the user from the lobby with the given id. If the user is the last
     * one, the lobby gets
     * deleted.
     *
     * @param user    The user who wants to leave the lobby
     * @param lobbyId The lobby id
     * @return True if leaving the lobby was successful, false if the lobby or the
     *         user are not found or if the user is already in the lobby.
     */
    public boolean leaveLobby(User user, int lobbyId) {
        Lobby lobby = Lobby.getLobby(lobbyId);

        if (lobby == null)
            return false;

        if (!lobby.removeUser(user)) {
            Lobby.deleteLobby(lobbyId);
        }

        ServerPrinter.displayInfo(user + " left lobby " + lobbyId);

        broadcastLobbies();
        return true;
    }

    /**
     * Removes the user from the lobby with the given id.
     * If the user was the last one in the lobby, the lobby gets deleted.
     * 
     * @param userInfo the user's UserInfo
     * @param lobbyId  the id of the lobby
     * 
     * @return true if successful, false if the user or the lobby isn't found or the
     *         user isn't in the lobby
     */
    public boolean leaveLobby(UserInfo userInfo, int lobbyId) {
        User user = User.userInfoToUser(userInfo);

        if (user == null)
            return false;

        Lobby lobby = Lobby.getLobby(lobbyId);

        if (lobby == null || !lobby.removeUser(user)) {
            sendMainEvent(userInfo, new LeaveLobbyError("You are not in the lobby"));
            return false;
        }

        ServerPrinter.displayInfo(user + " left lobby " + lobbyId);
        broadcastLobbies();
        return true;
    }

    /**
     * Starts the game by spawing the a game model and controller.
     *
     * @param user    The UserInfo of the user who tries to start the game
     * @param lobbyId The id of the lobby
     * @return True if the game is started successfully, false if the game doesn't
     *         exist, if the user is not the lobby manager of if the game was
     *         already started
     */
    public boolean startGame(UserInfo userInfo, int lobbyId) {
        User user = User.userInfoToUser(userInfo);

        if (!User.exists(userInfo))
            return false;

        Lobby lobby = Lobby.getLobby(lobbyId);

        // Try to start the game and save the eventual error message
        String error = null;
        if (lobby == null)
            error = "Invalid lobby id";
        else if (!lobby.getManager().equals(user))
            error = "You are not the manager of the lobby";
        else if (!lobby.startGame()) {
            if (lobby.gameStarted())
                error = "The match has already been started";
            else
                error = "Could't start game: not enough players";
        }

        // If there was an error, send an error event to the client
        if (error != null) {
            sendMainEvent(userInfo, new StartGameError(error));
            return false;
        }

        connectedPlayers.get(userInfo).setStatus(Status.IN_GAME);

        List<Observer> observers = connectedPlayers.entrySet().stream()
                .filter(entry -> lobby.contains(userInfo))
                .filter(entry -> entry.getValue().getStatus() == Status.IN_MENU)
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        observers = new CopyOnWriteArrayList<>(observers);

        ConcurrentHashMap<UserInfo, Supplier<Boolean>> isConnected = new ConcurrentHashMap<>();
        connectedPlayers
                .entrySet()
                .stream()
                .filter(e -> lobby.contains(userInfo))
                .forEach(
                        e -> isConnected.put(e.getKey(),
                                () -> connectedPlayers.get(e.getKey()).getStatus() == Status.IN_GAME));

        GameFlowManager gameFlowManager = new GameFlowManager(lobby, isConnected, observers);
        lobby.setGameFlowManager(gameFlowManager);

        // set gameflow to redirect requests to, on client handler only for the users in
        // the starting game
        // update playersInGame map
        connectedPlayers.entrySet().stream()
                .filter(entry -> lobby.contains(userInfo))
                .filter(entry -> entry.getValue().getStatus() == Status.IN_MENU)
                .forEach(
                        entry -> {
                            entry.getValue().setStatus(Status.IN_GAME);
                            try {
                                if (entry.getValue() instanceof SocketClientHandler) {
                                    SocketClientHandler client = (SocketClientHandler) entry.getValue();
                                    client.setGameFlowManager(gameFlowManager);
                                } else if (entry.getValue() instanceof RMIHandler) {
                                    RMIGameServer gameServer = new RMIGameServer(gameFlowManager,
                                            "gameServer" + lobbyId);
                                    RMIHandler client = (RMIHandler) entry.getValue();
                                    client.setGameServer(gameServer);
                                }
                            } catch (RemoteException e) {
                                ServerPrinter.displayError("Couldn't send connection event to");
                                e.printStackTrace();
                            }
                        });

        return true;
    }

    /**
     * Attempts the creation of a new lobby, fails if the user is already in another
     * one.
     * If unsuccessful, the server sends a AlreadyInLobbyErrorEvent to the client.
     * 
     * @param userInfo the user who wants to create a new lobby
     * @return the LobbyInfo of the created lobby if the attempt was successful,
     *         null if the user is already in a lobby
     * 
     * @see LobbyInfo
     * @see CreateLobbyError
     */
    public LobbyInfo createLobby(UserInfo userInfo) {
        User user = User.userInfoToUser(userInfo);

        if (user == null) {
            ServerPrinter
                    .displayInfo("Lobby creation request by " + userInfo + " failed: user not found in the server");
            return null;
        }

        if (Lobby.anyLobbyContains(user)) {
            ServerPrinter.displayInfo(
                    "Lobby creation request by " + userInfo + " failed: the user is already in another lobby");

            sendMainEvent(userInfo, new CreateLobbyError("You are in another lobby"));
            return null;
        }

        Lobby newLobby = new Lobby(user);
        broadcastLobbies();

        return newLobby.toLobbyInfo();
    }

    public void clientLogin(UserInfo userInfo, Client client) {
        if (connectedPlayers.containsKey(userInfo)) {
            ServerPrinter.displayInfo("User " + userInfo + " reconnecting");
            Client oldClient = connectedPlayers.get(userInfo);

            System.out.println("Old client: " + connectedPlayers.get(userInfo));
            System.out.println("State: " + oldClient.getStatus());

            connectedPlayers.put(userInfo, client);

            client.setStatus(Status.IN_MENU);

            try {
                if (oldClient.getStatus() == Status.OFFLINE) {
                    System.out.println("Reconnecting to main menu");
                    client.trasmitEvent(new LoginEvent(userInfo, null));
                    client.trasmitEvent(new LobbiesEvent(Lobby.getLobbies()));

                } else if (oldClient.getStatus() == Status.DISCONNETED_FROM_GAME) {
                    System.out.println("Reconnecting to game");
                    GameFlowManager gameFlowManager = Lobby.getLobby(userInfo).getGameFlowManager();
                    client.trasmitEvent(new ReconnectToGameEvent(gameFlowManager.gameModelUpdater.getSlimGameModel()));

                    // TODO: add as properties of reconnectToGameEvent the mapping <UserInfo, Token>

                    gameFlowManager.observers.remove(oldClient);
                    gameFlowManager.observers.add(client);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println(
                    "Error: User was not found in recent sessions or another user has the same user id. Assignign a new user id.");
            this.clientSignUp(userInfo.name, client);
        }
    }

    public UserInfo clientSignUp(String username, Client client) {
        User user;
        try {
            user = new User(username);
        } catch (IllegalArgumentException e) {
            user = new User(User.randomUsername(8));
        }

        UserInfo userInfo = user.toUserInfo();

        executorService.submit(() -> {
            try {
                client.trasmitEvent(new LoginEvent(userInfo, null));
                client.trasmitEvent(new LobbiesEvent(Lobby.getLobbies()));
            } catch (IOException e) {
                client.setStatus(Status.OFFLINE);
                System.err.println("Couldn't send userInfo event to " + userInfo);
                e.printStackTrace();
            }
        });

        connectedPlayers.put(userInfo, client);
        client.setStatus(Status.IN_MENU);

        return userInfo;
    }

    public void removeConnectedClient(MainViewActions clientMainView) {
        connectedPlayers.remove(clientMainView, true);
    }

    /**
     * This function sends a NewLobbiesEvent to all connected clients in the main
     * menu.
     */
    private void broadcastLobbies() {
        ServerPrinter.displayInfo("Broadcasting updated lobbies to players in the main menu");
        List<LobbyInfo> lobbies = Lobby.getLobbies();

        executorService.submit(
                () -> {
                    LobbiesEvent event = new LobbiesEvent(lobbies);

                    connectedPlayers.entrySet().stream()
                            .filter(entry -> entry.getValue().getStatus() == Status.IN_MENU)
                            .forEach(entry -> sendMainEvent(entry.getKey(), event));
                });
    }

    /**
     * Sends an event to the given user if they are in the main menu.
     * 
     * @param userInfo the user who will receive the event
     * @param event    the event to send
     */
    private void sendMainEvent(UserInfo userInfo, MainEvent event) {
        executorService.submit(() -> {
            ServerPrinter.displayDebug("Sending a " + event.getClass().getName() + " to " + userInfo);

            Client client = connectedPlayers.get(userInfo);
            if (client.getStatus() == Status.IN_MENU) {
                try {
                    client.trasmitEvent(event);
                } catch (IOException e) {
                    ServerPrinter.displayDebug("Could not send " + event.getClass().getName() + " to " + userInfo);
                }
            }
        });
    }
}
