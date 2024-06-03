package it.polimi.ingsw.controller.server;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.UserInfoEvent;
import it.polimi.ingsw.distributed.server.ClientHandler;
import it.polimi.ingsw.util.Pair;
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
import java.util.stream.Collectors;

/**
 * The server is implemented using the Singleton pattern. It handles users, lobbies and starting
 * games.
 */
public enum Server {
  INSTANCE;

  /** A map from the lobby id to the corresponding lobby */
  private final Map<Integer, Lobby> lobbies;

  /** The set containing all the users. */
  // TODO could be moved to the User class as a static set
  private final Set<User> users;

  /**
   * Links the Virtual Views to their status. The value is true if the client is in the menu, false
   * if it's in-game. When the client is in the menu, he receives updates on the list of lobbies.
   */
  private ConcurrentHashMap<Pair<UserInfo, MainViewActions>, Boolean> connectedPlayers;

  private final ExecutorService gameServersExecutor;

  Server() {
    this.lobbies = new HashMap<>();
    this.users = new HashSet<>();
    this.connectedPlayers = new ConcurrentHashMap<>();
    this.gameServersExecutor = Executors.newCachedThreadPool();
  }

  /**
   * Adds the user to the lobby with the given id.
   *
   * @param user The user who wants to join the lobby
   * @param lobbyId The lobby id
   * @return True if joining was successfull, false if the lobby doesn't exist or if the user is
   *     already in the lobby.
   */
  public boolean joinLobby(User user, int lobbyId) {
    synchronized (lobbies) {
      Lobby lobby = lobbies.get(lobbyId);

      boolean result = lobby != null && lobby.addUser(user);

      if (result) broadcastLobbies(getLobbies());

      return result;
    }
  }

  public boolean joinLobby(UserInfo userInfo, int lobbyId) {
    return joinLobby(userInfoToUser(userInfo), lobbyId);
  }

  /**
   * Removes the user from the lobby with the given id. If the user is the last one, the lobby gets
   * deleted.
   *
   * @param user The user who wants to leave the lobby
   * @param lobbyId The lobby id
   * @return True if leaving the lobby was successful, false if the lobby doesn't exist or if the
   *     user is already in the lobby.
   */
  public boolean leaveLobby(User user, int lobbyId) {
    synchronized (lobbies) {
      Lobby lobby = lobbies.get(lobbyId);

      if (lobby == null) return false;

      System.out.println("leaving lobby = " + lobby);

      if (!lobby.removeUser(user)) {
        lobbies.remove(lobbyId);
      }

      broadcastLobbies(getLobbies());

      return true;
    }
  }

  public boolean leaveLobby(UserInfo userInfo, int lobbyId) {
    User user = userInfoToUser(userInfo);
    return user != null && leaveLobby(user, lobbyId);
  }

  /**
   * Gives lobby informations.
   *
   * @return The list of lobbies as LobbyInfo classes.
   */
  public List<LobbyInfo> getLobbies() {
    synchronized (lobbies) {
      return lobbies.values().stream()
          .map(lobby -> new LobbyInfo(lobby))
          .collect(Collectors.toList());
    }
  }

  /**
   * Starts the game by spawing the single game model and controller.
   *
   * @param user The user who tries to start the game
   * @param lobbyId The id of the lobby.
   * @return True if the game is started successfully, false if the game doesn't exist, if the user
   *     is not the lobby manager of if the game was already started.
   */
  public boolean startGame(User user, int lobbyId) {
    synchronized (lobbies) {
      Lobby lobby = lobbies.get(lobbyId);

      if (lobby == null || user != lobby.getManager() || !lobby.startGame()) return false;

      // Aggiungi le view dei giocatori nel costruttore
      GameFlowManager gameFlowManager = new GameFlowManager(lobby);

      // TODO set gameflow on client handler only for the users in the starting game
      connectedPlayers.entrySet().stream()
          .filter(entry -> entry.getValue())
          .filter(entry -> lobby.getUsers().contains(entry.getKey().first))
          .forEach(
              entry -> {
                try {
                  if (entry.getKey().second instanceof ClientHandler) {
                    ClientHandler client = (ClientHandler) entry.getKey().second;
                    client.setGameFlowManager(gameFlowManager);
                  }
                } catch (RemoteException e) {
                  // TODO
                  System.err.println("Couldn't send connection event to");
                  e.printStackTrace();
                }
                ;
              });
    }

    return true;
  }

  public boolean startGame(UserInfo userInfo, int lobbyId) {
    User user = userInfoToUser(userInfo);
    return user != null && startGame(user, lobbyId);
  }

  /** Signup method to create an user. */
  public UserInfo signup(String name) {
    User user = new User(name);

    synchronized (users) {
      users.add(user);
    }

    return new UserInfo(user);
  }

  public LobbyInfo createLobby(UserInfo userInfo) {
    synchronized (lobbies) {
      synchronized (users) {
        System.out.println(users);
        System.out.println(userInfo);
        Optional<User> serverUser =
            users.stream().filter(user -> user.equals(userInfoToUser(userInfo))).findAny();

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
    // TODO handle reconnection
    connectedPlayers.put(new Pair<>(userInfo, clientMainView), true);
  }

  public UserInfo addConnectedClient(String username, MainViewActions clientMainView) {
    User user = new User(username);

    synchronized (users) {
      users.add(user);
    }

    UserInfo userInfo = new UserInfo(user);

    try {
      clientMainView.receiveEvent(new UserInfoEvent(userInfo));
    } catch (RemoteException e) {
      System.err.println("Couldn't send userInfo event to " + userInfo);
      e.printStackTrace();
    }

    connectedPlayers.put(new Pair<>(userInfo, clientMainView), true);

    return userInfo;
  }

  public void removeConnectedClient(MainViewActions clientMainView) {
    connectedPlayers.remove(clientMainView, true);
  }

  private User userInfoToUser(UserInfo userInfo) {
    synchronized (users) {
      return users.stream()
          .filter(user -> user.name.equals(userInfo.name) && user.id == userInfo.id)
          .findFirst()
          .orElse(null);
    }
  }

  private void broadcastLobbies(List<LobbyInfo> lobbies) {
    // TODO broadcast after updating lobbies
    new Thread(
            () -> {
              connectedPlayers.entrySet().stream()
                  .filter(entry -> entry.getValue())
                  .map(entry -> entry.getKey())
                  .forEach(
                      client -> {
                        try {
                          client.second.receiveEvent(new LobbiesEvent(lobbies));
                        } catch (RemoteException e) {
                          System.err.println("Error: Couldn't send message to " + client.first);
                          e.printStackTrace();
                        }
                      });
            })
        .start();
  }
}
