package it.polimi.ingsw.controller;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fusesource.jansi.AnsiConsole;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.usermanagement.Lobby;
import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.Status;
import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.game.GameStartedEvent;
import it.polimi.ingsw.distributed.events.main.CreateLobbyError;
import it.polimi.ingsw.distributed.events.main.JoinLobbyError;
import it.polimi.ingsw.distributed.events.main.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.main.LeaveLobbyError;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.LoginEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.events.main.ReconnectToGameEvent;
import it.polimi.ingsw.distributed.events.main.StartGameError;
import it.polimi.ingsw.distributed.interfaces.MainViewActions;
import it.polimi.ingsw.distributed.server.Client;
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

    public static final long MILLISEC_TIME_OUT = 5000L;

    /**
     * Links the UserInfo to their respective client.
     * From the Client instance, the server can get their status.
     */
    private final ConcurrentHashMap<UserInfo, Client> connectedPlayers = new ConcurrentHashMap<>();

    /**
     * This map keeps track of the last time a user sent a keep alive command.
     */
    private final ConcurrentHashMap<UserInfo, Long> lastKeepAliveMap = new ConcurrentHashMap<>();

    /**
     * ExecutorService for all Runnables created by the server.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Server entrypoint, must be called to start the Codex Naturalis server.
     * 
     * @param args not needed, they are ignored
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        if(!Config.setUp(args)) {
            System.out.println("Invalid arguments");
            return;
        }

        System.setProperty("java.rmi.server.hostname", Config.ServerIP);

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
            ServerPrinter.displayInfo("Check connection thread started");
            while (true) {
                checkConnections();
                checkKeepAlive();

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
                                // ServerPrinter.displayDebug("Sent keep alive to " + entry.getKey());
                            } catch (IOException e) {
                                entry.getValue().setDisconnectionStatus();
                                ServerPrinter
                                        .displayError("Couldn't send keep alive event to " + entry.getValue());
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
            } else if (lobby.gameStarted()) {
                error = "Game already started in lobby " + lobbyId;
            } else {
                error = "You are already in another lobby";
            }
        }

        // If there was an error, send an error event to the client
        if (error != null) {
            ServerPrinter.displayInfo(userInfo + "could not join. Error: " + error);
            sendMainEvent(userInfo, new JoinLobbyError(error));
            return false;
        }

        ServerPrinter.displayInfo(userInfo + " joined lobby " + lobbyId);

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
            ServerPrinter.displayInfo(user + " couldn't leave lobby " + lobbyId + ". The user was not in the lobby.");
            return false;
        }

        ServerPrinter.displayInfo(user + " left lobby " + lobbyId);
        broadcastLobbies();
        return true;
    }

    /**
     * Starts the game by spawing the a game model and controller.
     *
     * @param userInfo    The UserInfo of the user who tries to start the game
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
            ServerPrinter.displayInfo(userInfo + " cannot start the game. Error: " + error);
            return false;
        }

        List<UserInfo> usersInLobby = lobby.getUsers().stream()
                .map(User::toUserInfo)
                .collect(Collectors.toList());

        // Clients that are in the lobby
        List<Client> clients = connectedPlayers.entrySet().stream()
                .filter(entry -> usersInLobby.contains(entry.getKey()))
                .peek(entry -> sendMainEvent(entry.getKey(), new GameStartedEvent(usersInLobby)))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        // Create a map from userInfo to a Supplier that says if the user is connected
        // (needed by the game flow manager)
        ConcurrentHashMap<UserInfo, Supplier<Boolean>> isConnected = new ConcurrentHashMap<>();

        connectedPlayers
                .entrySet()
                .stream()
                .filter(e -> lobby.contains(userInfo))
                .forEach(
                        e -> isConnected.put(e.getKey(),
                                () -> connectedPlayers.get(e.getKey()).getStatus() == Status.IN_GAME));

        GameFlowManager gameFlowManager = new GameFlowManager(lobby, isConnected, new CopyOnWriteArrayList<>(clients));
        executorService.submit(gameFlowManager);
        lobby.setGameFlowManager(gameFlowManager);

        setUpClientsForGame(clients, gameFlowManager);

        ServerPrinter.displayInfo(userInfo + " started the game in lobby " + lobbyId);
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

        Client owldClient = connectedPlayers.get(userInfo);
        if (owldClient != null)
            ServerPrinter.displayInfo("Client status: " + owldClient.getStatus());

        if (!connectedPlayers.containsKey(userInfo)
                || (connectedPlayers.containsKey(userInfo)
                        && (connectedPlayers.get(userInfo).getStatus() == Status.IN_MENU
                                || connectedPlayers.get(userInfo).getStatus() == Status.IN_GAME))) {
            ServerPrinter.displayInfo(
                    "Error: User was not found in recent sessions or another user has the same user id. Assignign a new user id.");
            if (connectedPlayers.containsKey(userInfo)) {
                ServerPrinter
                        .displayInfo("User " + userInfo + " status: " + connectedPlayers.get(userInfo).getStatus());
            }

            this.clientSignUp(userInfo.name, client);
        } else if (connectedPlayers.containsKey(userInfo)) {
            ServerPrinter.displayInfo("User " + userInfo + " reconnecting");
            Client oldClient = connectedPlayers.get(userInfo);
            ServerPrinter.displayInfo("Old client status: " + oldClient.getStatus());

            try {
                if (oldClient.getStatus() == Status.OFFLINE) {
                    client.setStatus(Status.IN_MENU);

                    client.trasmitEvent(new LoginEvent(userInfo, null));
                    client.trasmitEvent(new LobbiesEvent(Lobby.getLobbies()));

                    updateKeepAlive(userInfo);
                    connectedPlayers.put(userInfo, client);

                    ServerPrinter.displayInfo("User " + userInfo + " reconnected to menu");

                } else if (oldClient.getStatus() == Status.DISCONNETED_FROM_GAME) {
                    Lobby lobby = Lobby.getLobby(userInfo);
                    GameFlowManager gameFlowManager = lobby.getGameFlowManager();
                    client.setStatus(Status.IN_GAME);

                    if (gameFlowManager.gameModelUpdater != null && gameFlowManager.userInfoToToken != null)
                        client.trasmitEvent(
                                new ReconnectToGameEvent(gameFlowManager.gameModelUpdater.getSlimGameModel(),
                                        gameFlowManager.userInfoToToken));
                    else if (gameFlowManager.gameModelUpdater != null)
                        client.trasmitEvent(
                                new ReconnectToGameEvent(gameFlowManager.gameModelUpdater.getSlimGameModel(),
                                        new HashMap<>()));
                    else
                        client.trasmitEvent(
                                new ReconnectToGameEvent(null,
                                        new HashMap<>()));

                    updateKeepAlive(userInfo);
                    connectedPlayers.put(userInfo, client);
                    setUpClientsForGame(Arrays.asList(client), gameFlowManager);

                    if (lobby.getGameFlowManager().gameModelUpdater != null)
                        lobby.getGameFlowManager().gameModelUpdater
                                .computeCardsPlayability(gameFlowManager.userInfoToToken.get(userInfo));

                    // TODO: when reconnecting add to the reconnectToGameEvent the mapping
                    // <UserInfo, Token>
                    gameFlowManager.observers.remove(oldClient);
                    gameFlowManager.observers.add(client);

                    ServerPrinter.displayInfo("Reconnectiong user " + userInfo + " to game");
                }
            } catch (IOException e) {
                ServerPrinter.displayError("Couldn't recconnect " + userInfo);
                ServerPrinter.displayError("Setting " + userInfo + " disconnected");
                if (oldClient.getStatus() == Status.IN_GAME) {
                    client.setStatus(Status.DISCONNETED_FROM_GAME);
                } else {
                    client.setStatus(Status.OFFLINE);
                    Lobby.removeUserIfGameNotStarted(client.userInfo.get());
                }
            }
        }
    }

    public UserInfo clientSignUp(String username, Client client) {
        User user;
        try {
            user = new User(username);
        } catch (IllegalArgumentException e) {
            ServerPrinter.displayInfo("User " + username + " is invalid. Assigning a random username.");
            user = new User(User.randomUsername(8));
        }

        UserInfo userInfo = user.toUserInfo();
        client.setStatus(Status.IN_MENU);

        executorService.submit(() -> {
            try {
                client.trasmitEvent(new LoginEvent(userInfo, null));
                client.trasmitEvent(new LobbiesEvent(Lobby.getLobbies()));
            } catch (IOException e) {
                client.setStatus(Status.OFFLINE);
                Lobby.removeUserIfGameNotStarted(client.userInfo.get());
                ServerPrinter.displayError("Couldn't send userInfo event to " + userInfo);
                ServerPrinter.displayError("Setting " + userInfo + " disconnected");
                e.printStackTrace();
            }
        });

        connectedPlayers.put(userInfo, client);
        client.setStatus(Status.IN_MENU);

        ServerPrinter.displayInfo("User " + userInfo + " signed up");
        return userInfo;
    }

    public void removeConnectedClient(MainViewActions clientMainView) {
        connectedPlayers.remove(clientMainView, true);
    }

    /**
     * This function sends a NewLobbiesEvent to all connected clients in the main
     * menu.
     */
    public void broadcastLobbies() {
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
                    client.setStatus(Status.OFFLINE);
                    Lobby.removeUserIfGameNotStarted(client.userInfo.get());
                    ServerPrinter.displayDebug("Could not send " + event.getClass().getName() + " to " + userInfo);
                }
            }
        });
    }

    /**
     * Sends a game event to the given user if they are in-game.
     * The function is public so it can be used by the GameFlowManager the event
     * only to the given user and not broadcast it to all users in the game.
     * This is particularly useful when sending errors to a certain user.
     * 
     * @param userInfo the user who will receive the event
     * @param event    the event to send
     */
    public void sendGameEvent(UserInfo userInfo, GameEvent event) {
        executorService.submit(() -> {
            ServerPrinter.displayDebug("Sending a " + event.getClass().getName() + " to " + userInfo);

            Client client = connectedPlayers.get(userInfo);
            if (client.getStatus() == Status.IN_GAME) {
                try {
                    client.transmitEvent(event);
                } catch (IOException e) {
                    client.setStatus(Status.DISCONNETED_FROM_GAME);
                    ServerPrinter.displayDebug("Could not send " + event.getClass().getName() + " to " + userInfo);
                }
            }
        });
    }

    /**
     * This method sets up the clients. It is used to start a game or to reconnect
     * players to it.
     * On socket client handlers sets the game corresponding gameFlowManager.
     * On rmi client handler passes the game server remote object linked to the
     * gameFlowManager of the game.
     * 
     * @param clients
     * @param gameFlowManager
     */
    private void setUpClientsForGame(List<Client> clients, GameFlowManager gameFlowManager) {
        for (Client c : clients) {
            // Set the status of the client to IN_GAME
            c.setStatus(Status.IN_GAME);

            // Init connection to the game
            try {
                if (c instanceof SocketClientHandler) {
                    SocketClientHandler client = (SocketClientHandler) c;
                    client.setGameFlowManager(gameFlowManager);
                } else if (c instanceof RMIHandler) {
                    RMIHandler client = (RMIHandler) c;
                    client.setGameServer(new RMIGameServer(gameFlowManager));
                }
            } catch (RemoteException e) {
                ServerPrinter.displayError("Couldn't send connection event to " + c);
                ServerPrinter.displayError("Setting " + c + " disconnected");
                c.setDisconnectionStatus();
            }
        }
    }

    /**
     * 
     * @param userInfo
     */
    private void checkKeepAlive() {
        lastKeepAliveMap.entrySet().stream()
                .filter(entry -> connectedPlayers.get(entry.getKey()).getStatus() == Status.IN_MENU
                        || connectedPlayers.get(entry.getKey()).getStatus() == Status.IN_GAME)
                .filter(entry -> System.currentTimeMillis() - entry.getValue() > Server.MILLISEC_TIME_OUT)
                .filter(entry -> lastKeepAliveMap.containsKey(entry.getKey()))
                .forEach(entry -> {
                    ServerPrinter.displayInfo("User " + entry.getKey() + " disconnected for inactivity");
                    connectedPlayers.get(entry.getKey()).setDisconnectionStatus();
                });

    }

    /**
     * This function is used by the handler to update the last keep alive time of a
     * client.
     * 
     * @param userInfo the user who sent the keep alive
     */
    public void updateKeepAlive(UserInfo userInfo) {
        lastKeepAliveMap.put(userInfo, System.currentTimeMillis());
    }

}
