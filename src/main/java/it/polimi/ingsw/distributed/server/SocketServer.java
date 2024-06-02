package it.polimi.ingsw.distributed.server;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

  private final ServerSocket serverSocket;
  private final ExecutorService executorService;

  private final ConcurrentHashMap<UserInfo, ClientHandler> connections;

  public SocketServer(int port) throws IOException {
    this.executorService = Executors.newCachedThreadPool();
    this.serverSocket = new ServerSocket(port);
    this.connections = new ConcurrentHashMap<>();

    new Thread(
            () -> {
              while (true) {
                try {
                  Socket socket = serverSocket.accept();

                  ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                  Command command = (Command) in.readObject();

                  ClientHandler connection = new ClientHandler(socket);

                  if (command instanceof ConnectionCommand) {
                    ConnectionCommand connectionEvent = (ConnectionCommand) command;
                    String username = connectionEvent.username;

                    UserInfo userInfo = Server.INSTANCE.addConnectedClient(username, connection);
                    connections.put(userInfo, connection);
                  } else if (command instanceof ReconnectionCommand) {
                    ReconnectionCommand reconnectionCommand = (ReconnectionCommand) command;
                    UserInfo userInfo = reconnectionCommand.userInfo;

                    Server.INSTANCE.addReconnectedClient(userInfo, connection);
                    connections.put(userInfo, connection);
                  } else {
                    System.err.println("Unrecognized request on socket server");
                  }

                  executorService.submit(connection);

                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            })
        .start();
  }

  public ConcurrentHashMap<UserInfo, ClientHandler> getConnections() {
    return connections;
  }
}
