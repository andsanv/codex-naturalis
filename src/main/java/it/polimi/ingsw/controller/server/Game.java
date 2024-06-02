package it.polimi.ingsw.controller.server;

import it.polimi.ingsw.controller.GameFlowManager;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Game class represents a single game. It contains the in-game connections to the clients, the
 * controller and the model.
 */
public class Game implements Runnable {
  private Lobby lobby;
  private Map<User, Boolean> isConnected;

  private long checkInterval = 2; // seconds

  private GameFlowManager gameFlowManager;

  /**
   * When created, all clients are disconnected by default. The method joinGame() of Server must be
   * called to join the match.
   *
   * @param lobby lobby of the game
   */
  public Game(Lobby lobby) {
    this.lobby = lobby;
    this.isConnected =
        lobby.getUsers().stream().collect(Collectors.toMap(Function.identity(), u -> false));
    this.gameFlowManager = new GameFlowManager(lobby);
  }

  @Override
  public void run() {
    Thread checkConnectionsThread = new Thread(this::checkConnections);
    checkConnectionsThread.setDaemon(true);
    checkConnectionsThread.start();
  }

  public void checkConnections() {
    while (true) {
      for (User user : isConnected.keySet()) {
        boolean connected = true;
        // TODO check connection that sets connected flag to true if client responds
        isConnected.put(user, connected);

        try {
          Thread.sleep(checkInterval * 1000);
        } catch (InterruptedException e) {
        }
      }
    }
  }
}
