package it.polimi.ingsw.distributed.server;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.UserInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Accepts incoming connections to a single game. Implements Runnable, so it can be submitted to an
 * executor, since a SocketGameServer is created for each game.
 */
public class SocketGameServer implements Runnable {
  private static int lastUsedPort = 15789;

  private static int maxBindAttempts = 10;

  private ServerSocket serverSocket;
  private final GameFlowManager gameFlowManager;

  private final ExecutorService executorService;

  private final ConcurrentHashMap<UserInfo, GameSocketConnection> connections;

  private int port;

  public SocketGameServer(GameFlowManager gameFlowManager) throws IOException {
    int max_attempts = maxBindAttempts;

    synchronized (SocketGameServer.class) {
      lastUsedPort++;
      this.port = lastUsedPort;

      while (max_attempts-- >= 0) {
        try {
          this.serverSocket = new ServerSocket(lastUsedPort);
          break;
        } catch (BindException e) {
          synchronized (SocketGameServer.class) {
            lastUsedPort++;
            this.port = lastUsedPort;
          }
        }
      }
    }

    if (max_attempts < 0) {
      throw new IOException("Could not bind to any port");
    }

    this.serverSocket = new ServerSocket(lastUsedPort);
    this.gameFlowManager = gameFlowManager;
    this.executorService = Executors.newCachedThreadPool();
    this.connections = new ConcurrentHashMap<>();
  }

  public int getPort() {
    return this.port;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Socket socket = serverSocket.accept();

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        UserInfo userInfo = (UserInfo) in.readObject();

        // TODO handle game socket connection creation
        GameSocketConnection connection = new GameSocketConnection(socket, in, gameFlowManager);
        executorService.submit(connection);
        connections.put(userInfo, connection);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
