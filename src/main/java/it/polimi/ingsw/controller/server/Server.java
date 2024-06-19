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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.rmi.RMIMainView;
import it.polimi.ingsw.distributed.events.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.main.AlreadyInLobbyErrorEvent;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.MainErrorEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.events.main.ReconnectToGameEvent;
import it.polimi.ingsw.distributed.events.main.UserInfoEvent;
import it.polimi.ingsw.distributed.server.rmi.RMIGameServer;
import it.polimi.ingsw.distributed.server.socket.ClientHandler;
import it.polimi.ingsw.util.Pair;

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
   * Links the Virtual Views to their status.
   * The value is true if the client is in the menu, false if it's in-game.
   * When the client is in the menu, he receives updates on the list of lobbies.
   */
  private ConcurrentHashMap<UserInfo, Pair<MainViewActions, AtomicBoolean>> connectedPlayers;
  private ConcurrentHashMap<UserInfo, Boolean> playersInMenu;

  private final ExecutorService executorService;

  /**
   * Constructor of the only instance of the Server singleton.
   */
  Server() {
    this.lobbies = new HashMap<>();
    this.users = new HashSet<>();
    this.connectedPlayers = new ConcurrentHashMap<>();
    this.playersInMenu = new ConcurrentHashMap<>();
    this.lobbyToGameFlowManager = new HashMap<>();
    this.executorService = Executors.newCachedThreadPool();
    checkConnections();
  }

  public void checkConnections() {
    executorService.submit(
        () -> {
          while (true) {
            connectedPlayers.entrySet().stream()
                .filter(entry -> entry.getValue().second.get())
                .forEach(
                    entry -> {
                      try {
                        entry.getValue().first.receiveEvent(new KeepAliveEvent());
                        System.out.println("Sent keep alive to " + entry.getKey());
                      } catch (IOException e) {
                        AtomicBoolean atomicBoolean = connectedPlayers.get(entry.getKey()).second;
                        atomicBoolean.set(false);

                        System.err.println("Error: Couldn't send message to " + entry.getValue().first);
                        System.err.println("Client " + entry.getKey() + " is disconnected");
                      }
                    });
            try {
              Thread.sleep(1500);
            } catch (InterruptedException e) {
              e.printStackTrace();
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
      if (getLobbies().stream().anyMatch(l -> l.users.contains(userInfo)))
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

      // Aggiungi le view dei giocatori nel costruttore
      List<Observer> observers = connectedPlayers.entrySet().stream()
          .filter(entry -> entry.getValue().second.get())
          .filter(entry -> lobby.getUsers().contains(userInfoToUser(entry.getKey())))
          .map(entry -> entry.getValue().first)
          .collect(Collectors.toList());

      ConcurrentHashMap<UserInfo, AtomicBoolean> isConnected = new ConcurrentHashMap<>();
      connectedPlayers
          .entrySet()
          .stream()
          .filter(e -> lobby.getUsers().contains(userInfoToUser(e.getKey())))
          .forEach(e -> isConnected.put(e.getKey(), e.getValue().second));

      GameFlowManager gameFlowManager = new GameFlowManager(lobby, isConnected, observers);
      lobbyToGameFlowManager.put(lobby, gameFlowManager);

      // set gameflow to redirect requests to, on client handler only for the users in
      // the starting game
      // update playersInGame map
      connectedPlayers.entrySet().stream()
          .filter(entry -> entry.getValue().second.get())
          .filter(entry -> lobby.getUsers().contains(userInfoToUser(entry.getKey())))
          .forEach(
              entry -> {
                playersInMenu.put(entry.getKey(), false);
                try {
                  if (entry.getValue().first instanceof ClientHandler) {
                    ClientHandler client = (ClientHandler) entry.getValue().first;
                    client.setGameFlowManager(gameFlowManager);
                  } else if (entry.getValue().first instanceof RMIMainView) {
                    RMIGameServer gameServer = new RMIGameServer(gameFlowManager, "gameServer" + lobbyId);
                    RMIMainView client = (RMIMainView) entry.getValue().first;
                    client.setGameServer(gameServer);
                  }
                } catch (RemoteException e) {
                  // TODO
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
        if (getLobbies().stream().anyMatch(lobby -> lobby.users.contains(userInfo))) {
          System.err.println("Lobby creation request by " + userInfo + " failed: the user is already in another lobby");
          try {
            connectedPlayers.get(userInfo).first.receiveEvent(new AlreadyInLobbyErrorEvent());
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

  public void addReconnectedClient(UserInfo userInfo, MainViewActions clientMainView) {
    System.out.println(clientMainView);
    if (playersInMenu.containsKey(userInfo)) {
      MainViewActions oldMainViewActions = connectedPlayers.get(userInfo).first;
      AtomicBoolean atomicBoolean = connectedPlayers.get(userInfo).second;

      connectedPlayers.put(userInfo, new Pair<MainViewActions, AtomicBoolean>(clientMainView, atomicBoolean));
      atomicBoolean.set(true);

      try {
        if (playersInMenu.get(userInfo))
          clientMainView.receiveEvent(new LobbiesEvent(getLobbies()));
        else {
          GameFlowManager gameFlowManager = lobbyToGameFlowManager.get(lobbies.values().stream()
              .filter(lobby -> lobby.getUsers().contains(userInfoToUser(userInfo)))
              .findFirst()
              .orElse(null));
          clientMainView.receiveEvent(new ReconnectToGameEvent(gameFlowManager.gameModelUpdater.getSlimGameModel()));

          gameFlowManager.observers.remove(oldMainViewActions);
          gameFlowManager.observers.add(clientMainView);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      System.out.println("Error: User not found in recent sessions, assigning new user");
      addConnectedClient(userInfo.name, clientMainView);
    }
  }

  public UserInfo addConnectedClient(String username, MainViewActions clientMainView) {
    User user = new User(username);

    synchronized (users) {
      users.add(user);
    }

    UserInfo userInfo = new UserInfo(user);

    // TODO da mettere in una lambda da passare all'executor service perché
    // bloccanti
    try {
      clientMainView.receiveEvent(new UserInfoEvent(userInfo));
      clientMainView.receiveEvent(new LobbiesEvent(getLobbies()));
    } catch (IOException e) {
      System.err.println("Couldn't send userInfo event to " + userInfo);
      e.printStackTrace();
    }

    connectedPlayers.put(userInfo, new Pair<>(clientMainView, new AtomicBoolean(true)));

    playersInMenu.put(userInfo, true);

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
              .filter(entry -> playersInMenu.get(entry.getKey()) != null)
              .filter(entry -> playersInMenu.get(entry.getKey()))
              .filter(entry -> entry.getValue().second.get())
              .forEach(
                  entry -> {
                    try {
                      System.out.println("Sending lobbies to " + entry.getKey());
                      System.out.println(entry.getValue().first);
                      System.out.println(entry.getValue().second);
                      entry.getValue().first.receiveEvent(new LobbiesEvent(lobbies));
                    } catch (IOException e) {
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

      Pair<MainViewActions, AtomicBoolean> client = connectedPlayers.get(userInfo);
      if (client.second.get()) {
        try {
          client.first.receiveEvent(new MainErrorEvent(error));
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

      Pair<MainViewActions, AtomicBoolean> client = connectedPlayers.get(userInfo);
      if (client.second.get()) {
        try {
          client.first.receiveEvent(event);
        } catch (IOException e) {
          System.err.println("Could not send " + event.getClass().getName() + " to " + userInfo);
        }
      }
    });
  }
}
