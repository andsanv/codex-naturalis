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


/**
 * This class represents the server side of the socket connection.
 * It is the entry server, that keeps listening for new client, 
 * delegating the communication to the ClientHandler class.
 */
public class SocketServer {

  /** The server socket instance */
  private final ServerSocket serverSocket;

  /** The executor service to manage client handlers */
  private final ExecutorService executorService;

  /** Map from user info to corresponding client handler */
  private final ConcurrentHashMap<UserInfo, ClientHandler> connections;

  /**
   * This constructor is the starting point for the server.
   * It creates a new socket, initializes the attributes.
   * It also calls the startAcceptingClients method and ends.
   * @param port the port which the server will listen on
   * @throws IOException if the server cannot be started
   */
  public SocketServer(int port) throws IOException {
    this.executorService = Executors.newCachedThreadPool();
    this.serverSocket = new ServerSocket(port);
    this.connections = new ConcurrentHashMap<>();

    System.out.println("Socket server started on port " + port);

    startAcceptingClients();
  }

  /**
   * This method starts a thread that keeps listening for new clients.
   * Once a new client is accpted, object streams are configured and the client handler is created.
   * The first command received must be either a ConnectionCommand or a ReconnectionCommand. If not request is ignored.
   * If the command is a ConnectionCommand, the server controller creates a new UserInfo for the client.
   * If the command is Reconnection command, the UserInfo is contained in the request.
   * In both cases, the UserInfo and the ClientHandler are added to the connections map.
   */
  public void startAcceptingClients() {
    new Thread(
            () -> {
              while (true) {
                try {
                  Socket socket = serverSocket.accept();
                  System.out.println("New connection from " + socket.getInetAddress() + ":" + socket.getPort());
                  
                  ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                  out.flush();
                  ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                  ClientHandler connection = new ClientHandler(out, in);

                  Command command = (Command) in.readObject();

                  System.out.println("Received command: " + command);

                  if (command instanceof ConnectionCommand) {
                    ConnectionCommand connectionCommand = (ConnectionCommand) command;
                    String username = connectionCommand.username;

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

                  executorService.submit(connection);

                } catch (Exception e) {
                  System.err.println("Error while accepting client: " + e.getMessage());
                  
                }
              }
            })
        .start();
  }

  /**
   * This method returns the connections map.
   * @return the connections map
   */
  public ConcurrentHashMap<UserInfo, ClientHandler> getConnections() {
    return connections;
  }
}
