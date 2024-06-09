package it.polimi.ingsw.distributed.server.socket;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    System.out.println("Socket server started on port " + port);

    new Thread(
            () -> {
              while (true) {
                try {
                  Socket socket = serverSocket.accept();
                  System.out.println("New connection from " + socket.getInetAddress());
                  
                  ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                  out.flush();
                  ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                  Command command = (Command) in.readObject();

                  ClientHandler connection = new ClientHandler(out, in);

                  // TODO do not use instanceof, cast directly, unify connection event with type conn/reconn
                  if (command instanceof ConnectionCommand) {
                    ConnectionCommand connectionEvent = (ConnectionCommand) command;
                    String username = connectionEvent.username;

                    System.out.println("New connection from " + username);

                    UserInfo userInfo = Server.INSTANCE.addConnectedClient(username, connection);
                    connections.put(userInfo, connection);
                  } else if (command instanceof ReconnectionCommand) {
                    ReconnectionCommand reconnectionCommand = (ReconnectionCommand) command;
                    UserInfo userInfo = reconnectionCommand.userInfo;

                    System.out.println("Reconnection from " + userInfo);

                    Server.INSTANCE.addReconnectedClient(userInfo, connection);
                    connections.put(userInfo, connection);
                  } else {
                    System.err.println("Unrecognized request on socket server");
                  }

                  System.out.println("Connection command received");
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
