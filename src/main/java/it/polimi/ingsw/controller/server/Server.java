package it.polimi.ingsw.controller.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.Status;
import it.polimi.ingsw.distributed.events.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.main.AlreadyInLobbyErrorEvent;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.MainErrorEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.events.main.ReconnectToGameEvent;
import it.polimi.ingsw.distributed.events.main.LoginEvent;
import it.polimi.ingsw.distributed.server.rmi.RMIGameServer;
import it.polimi.ingsw.distributed.server.rmi.RMIHandler;
import it.polimi.ingsw.distributed.server.socket.SocketClientHandler;

/**
 * The server is implemented using the Singleton pattern. It handles users,
 * lobbies and starting
 * games.
 */
public enum Server {
  INSTANCE;

  /** A map from the lobby id to the corresponding lobby */
  private final Map<Integer, Lobby> lobbies;
  private final Map<Lobby, GameFlowManager> lobbyToGameFlowManager;

  /** The set containing all the users. */
  private final Set<User> users;

  /**
   * Links the UserInfo to their respective client.
   * From the Client instance, the server can get their status.
   */
  private final ConcurrentHashMap<UserInfo, Client> connectedPlayers;

  private final ExecutorService executorService;

  /**
   * Constructor of the only instance of the Server singleton.
   */
  Server() {
    this.lobbies = new HashMap<>();
    this.users = new HashSet<>();
    this.connectedPlayers = new ConcurrentHashMap<>();
    this.lobbyToGameFlowManager = new HashMap<>();
    this.executorService = Executors.newCachedThreadPool();
    checkConnections();
  }

  

  public void checkConnections() {
    executorService.submit(
        () -> {
          while (true) {
            connectedPlayers.entrySet().stream()
                .filter(entry -> {
                  return entry.getValue().getStatus() == Status.IN_GAME ||
                      entry.getValue().getStatus() == Status.IN_MENU;
                })
                .forEach(
                    entry -> {
                      try {
                        entry.getValue().receiveEvent(new KeepAliveEvent());
                        System.out.println("Sent keep alive to " + entry.getKey());
                      } catch (IOException e) {
                        if (entry.getValue().getStatus() == Status.IN_GAME) {
                          entry.getValue().setStatus(Status.DISCONNETED_FROM_GAME);
                        } else if (entry.getValue().getStatus() == Status.IN_MENU)
                          entry.getValue().setStatus(Status.OFFLINE);

                        System.err.println("Error: Couldn't send message to " + entry.getValue());
                        System.err.println("Client " + entry.getKey() + " is disconnected");
                      }
                    });
            try {
              Thread.sleep(1500);
            } catch (InterruptedException e) {
              System.err.println("Check connection crashed");
            }
          }
        });
  }

  /**
   * Adds the user to the lobby with the given id.
   *
   * @param user    The user who wants to join the lobby
   * @param lobbyId The lobby id
   * @return True if joining was successfull, false if the lobby doesn't exist or
   *         if the user is
   *         already in the lobby.
   */
  public boolean joinLobby(UserInfo userInfo, int lobbyId) {
    synchronized (lobbies) {
      Lobby lobby = lobbies.get(lobbyId);

      // Try to add the user to the lobby
      String error = null;
      if (isInLobby(userInfo))
        error = "Couldn't join the lobby: you are already in another one";
      else if (lobby == null)
        error = "Couldn't join the lobby: the lobby doesn't exist";
      else if (!lobby.addUser(userInfoToUser(userInfo))) {
        if (lobby.isFull()) {
          error = "Couldn't join the lobby: the lobby is full";
        } else {
          error = "Couldn't join the lobby: you are already in the lobby";
        }
      }

      // If there was an error, send an error event to the client
      if (error != null) {
        sendMainError(userInfo, error);
        return false;
      }

      broadcastLobbies(getLobbies());
      return true;
    }
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
    synchronized (lobbies) {
      Lobby lobby = lobbies.get(lobbyId);

      if (lobby == null)
        return false;

      System.out.println("leaving lobby = " + lobby);

      if (!lobby.removeUser(user)) {
        lobbies.remove(lobbyId);
      }

      broadcastLobbies(getLobbies());

      return true;
    }
  }

  /**
   * Deletes a lobby.
   * Must be called when the match ends.
   * 
   * @param lobbyId the id of the lobby
   */
  public void deleteLobby(int lobbyId) {
    synchronized (lobbies) {
      lobbies.remove(lobbyId);
    }
  }

  /**
   * Removes the given user from the given lobby.
   * 
   * @param userInfo the user's UserInfo
   * @param lobbyId  the id of the lobby
   * 
   * @return true if successful, false if the user is not found or isn't in the
   *         lobby
   */
  public boolean leaveLobby(UserInfo userInfo, int lobbyId) {
    User user = userInfoToUser(userInfo);
    return user != null && leaveLobby(user, lobbyId);
  }

  /**
   * Gives lobby informations.
   *
   * @return The list of lobbies as LobbyInfo classes.
   * 
   * @see LobbyInfo
   */
  public List<LobbyInfo> getLobbies() {
    synchronized (lobbies) {
      return lobbies.values().stream()
          .map(lobby -> new LobbyInfo(lobby))
          .collect(Collectors.toList());
    }
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
    User user = userInfoToUser(userInfo);

    if (user == null)
      return false;

    synchronized (lobbies) {
      Lobby lobby = lobbies.get(lobbyId);

      // Try to start the game and save the eventual error message
      String error = null;
      if (lobby == null)
        error = "Could't start game: invalid lobby id";
      else if (user != lobby.getManager())
        error = "Could't start game: you are not the leader of the lobby";
      else if (!lobby.startGame()) {
        if (lobby.gameStarted)
          error = "The match has already been started";
        else
          error = "Could't start game: not enough players";
      }

      // If there was an error, send an error event to the client
      if (error != null) {
        sendMainError(userInfo, error);
        return false;
      }

      connectedPlayers.get(userInfo).setStatus(Status.IN_GAME);

      List<Observer> observers = connectedPlayers.entrySet().stream()
          .filter(entry -> lobby.getUsers().contains(userInfoToUser(entry.getKey())))
          .filter(entry -> entry.getValue().getStatus() == Status.IN_MENU)
          .map(entry -> entry.getValue())
          .collect(Collectors.toList());

      observers = new CopyOnWriteArrayList<>(observers);

      ConcurrentHashMap<UserInfo, Supplier<Boolean>> isConnected = new ConcurrentHashMap<>();
      connectedPlayers
          .entrySet()
          .stream()
          .filter(e -> lobby.getUsers().contains(userInfoToUser(e.getKey())))
          .forEach(e -> isConnected.put(e.getKey(), ()-> connectedPlayers.get(e.getKey()).getStatus() == Status.IN_GAME));

      GameFlowManager gameFlowManager = new GameFlowManager(lobby, isConnected, observers);
      lobbyToGameFlowManager.put(lobby, gameFlowManager);

      // set gameflow to redirect requests to, on client handler only for the users in
      // the starting game
      // update playersInGame map
      connectedPlayers.entrySet().stream()
          .filter(entry -> lobby.getUsers().contains(userInfoToUser(entry.getKey())))
          .filter(entry -> entry.getValue().getStatus() == Status.IN_MENU)
          .forEach(
              entry -> {
                entry.getValue().setStatus(Status.IN_GAME);
                try {
                  if (entry.getValue() instanceof SocketClientHandler) {
                    SocketClientHandler client = (SocketClientHandler) entry.getValue();
                    client.setGameFlowManager(gameFlowManager);
                  } else if (entry.getValue() instanceof RMIHandler) {
                    RMIGameServer gameServer = new RMIGameServer(gameFlowManager, "gameServer" + lobbyId);
                    RMIHandler client = (RMIHandler) entry.getValue();
                    client.setGameServer(gameServer);
                  }
                } catch (RemoteException e) {
                  System.err.println("Couldn't send connection event to");
                  e.printStackTrace();
                }
              });
    }

    return true;
  }

  /**
   * Signup method to create an user.
   * 
   * @param name the user name
   * @return the UserInfo of the new player.
   * 
   * @see UserInfo
   */
  public UserInfo signup(String name) {
    User user = new User(name);

    synchronized (users) {
      users.add(user);
    }

    return new UserInfo(user);
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
   * @see AlreadyInLobbyErrorEvent
   */
  public LobbyInfo createLobby(UserInfo userInfo) {
    synchronized (lobbies) {
      synchronized (users) {
        if (isInLobby(userInfo)) {
          System.err.println("Lobby creation request by " + userInfo + " failed: the user is already in another lobby");
          try {
            connectedPlayers.get(userInfo).receiveEvent(new AlreadyInLobbyErrorEvent());
          } catch (IOException e) {
            e.printStackTrace();
          }
          return null;
        }

        System.out.println(users);
        System.out.println(userInfo);
        Optional<User> serverUser = users.stream().filter(user -> user.equals(userInfoToUser(userInfo))).findAny();

        if (!serverUser.isPresent()) {
          System.err.println("Error: User not found");
          return null;
        }

        Lobby newLobby = new Lobby(serverUser.get());
        lobbies.put(newLobby.id, newLobby);

        broadcastLobbies(getLobbies());

        return new LobbyInfo(newLobby);
      }
    }
  }

  private boolean isInLobby(UserInfo userInfo) {
    return getLobbies().stream().anyMatch(lobby -> lobby.users.contains(userInfo));
  }

  public void clientLogin(UserInfo userInfo, Client client) {
    if (connectedPlayers.containsKey(userInfo)) {
      System.out.println("Reconnecting user " + userInfo);
      Client oldClient = connectedPlayers.get(userInfo);

      System.out.println("Old client: " + connectedPlayers.get(userInfo));
      System.out.println("State: " + oldClient.getStatus());
      
      connectedPlayers.put(userInfo, client);

      client.setStatus(Status.IN_MENU);

      try {
        if (oldClient.getStatus() == Status.OFFLINE){
          System.out.println("Reconnecting to main menu");
          client.receiveEvent(new LobbiesEvent(getLobbies()));
        }
        else if (oldClient.getStatus() == Status.DISCONNETED_FROM_GAME) {
          System.out.println("Reconnecting to game");
          GameFlowManager gameFlowManager = lobbyToGameFlowManager.get(lobbies.values().stream()
              .filter(lobby -> lobby.getUsers().contains(userInfoToUser(userInfo)))
              .findFirst()
              .orElse(null));
          client.receiveEvent(new ReconnectToGameEvent(gameFlowManager.gameModelUpdater.getSlimGameModel()));

          // TODO: add as properties of reconnectToGameEvent the mapping <UserInfo, Token>

          gameFlowManager.observers.remove(oldClient);
          gameFlowManager.observers.add(client);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.err.println("Error: User was not found in recent sessions or another user has the same user id. Assignign a new user id.");
      try {
        client.receiveEvent(new MainErrorEvent("Error: User was not found in recent sessions or another user has the same user id. Assignign a new user id."));
      } catch (IOException e) {
        e.printStackTrace();
      }
      this.clientSignUp(userInfo.name, client);
    }
  }

  public UserInfo clientSignUp(String username, Client client) {
    User user = new User(username);

    synchronized (users) {
      users.add(user);
    }

    UserInfo userInfo = new UserInfo(user);

    executorService.submit(() -> {
      try {
        client.receiveEvent(new LoginEvent(userInfo, null));
        client.receiveEvent(new LobbiesEvent(getLobbies()));
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
   * Finds the User matching the given UserInfo.
   * If no user is found this method returns null.
   * 
   * @param userInfo the player UserInfo
   * @return the User if found, null otherwise
   * 
   * @see User
   * @see UserInfo
   */
  private User userInfoToUser(UserInfo userInfo) {
    synchronized (users) {
      return users.stream()
          .filter(user -> user.name.equals(userInfo.name) && user.id == userInfo.id)
          .findFirst()
          .orElse(null);
    }
  }

  /**
   * This function sends a NewLobbiesEvent to all connected clients in the main
   * menu.
   * 
   * @param lobbies the list of LobbyInfo of the existing lobbies
   */
  private void broadcastLobbies(List<LobbyInfo> lobbies) {
    System.out.println("Broadcasting lobbies to all connected players in the main menu");

    executorService.submit(
        () -> {
          connectedPlayers.entrySet().stream()
              .filter(entry -> entry.getValue().getStatus() == Status.IN_MENU)
              .forEach(
                  entry -> {
                    try {
                      System.out.println("Sending lobbies to " + entry.getKey());
                      entry.getValue().receiveEvent(new LobbiesEvent(lobbies));
                    } catch (IOException e) {
                      entry.getValue().setStatus(Status.OFFLINE);
                      System.err.println("Error: Couldn't send message to " + entry.getKey());
                    }
                  });
        });
  }

  /**
   * Sends an error to the given user if they are in the main menu.
   * 
   * @param userInfo the user who will receive the error
   * @param error    the error message
   */
  private void sendMainError(UserInfo userInfo, String error) {
    executorService.submit(() -> {
      System.out.println("Sending error to " + userInfo + ": " + error);

      Client client = connectedPlayers.get(userInfo);
      if (client.getStatus() == Status.IN_MENU){
        try {
          client.receiveEvent(new MainErrorEvent(error));
        } catch (IOException e) {
          System.err.println("Could not send error message to " + userInfo);
        }
      }
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
      System.out.println("Sending a " + event.getClass().getName() + " to " + userInfo);

      Client client = connectedPlayers.get(userInfo);
      if (client.getStatus() == Status.IN_MENU) {
        try {
          client.receiveEvent(event);
        } catch (IOException e) {
          System.err.println("Could not send " + event.getClass().getName() + " to " + userInfo);
        }
      }
    });
  }
}
