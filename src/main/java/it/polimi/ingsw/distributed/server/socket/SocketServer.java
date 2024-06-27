package it.polimi.ingsw.distributed.server.socket;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerPrinter;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
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

  /** The executor service to manage accepting clients and client handlers */
  private final ExecutorService executorService;

  /** Map from user info to corresponding client handler */
  private final ConcurrentHashMap<UserInfo, SocketClientHandler> connections;

  /**
   * This constructor is the starting point for the server.
   * It creates a new socket, initializes the attributes.
   * It also calls the startAcceptingClients method and ends.
   * 
   * @param port the port which the server will listen on
   * @throws IOException if the server cannot be started
   */
  public SocketServer(int port) throws IOException {
    this.executorService = Executors.newCachedThreadPool();
    this.serverSocket = new ServerSocket(port);
    this.connections = new ConcurrentHashMap<>();

    executorService.submit(this::startAcceptingClients);
  }

  /**
   * This method keeps listening for new clients.
   * Once a new client is accepted,
   */
  public void startAcceptingClients() {
    while (true) {
      try {
        Socket socket = serverSocket.accept();

        executorService.submit(()-> handleSingleClientConnection(socket));

      } catch (Exception e) {
        ServerPrinter.displayError("Error while accepting new client");
      }
    }
  }

  /**
   * This function handles a single client connection setup.
   * Object streams are configured and the client handler is created.
   * 
   * The first command received must be either a ConnectionCommand or a
   * ReconnectionCommand. If not request is ignored.
   * 
   * If the command is a ConnectionCommand, the server controller assigns a new
   * UserInfo for the client.
   * If the command is Reconnection command, the UserInfo is contained in the
   * request and the server controller will analyze it.
   * 
   * In both cases, the UserInfo and the ClientHandler are added to the
   * connections map.
   */
  public void handleSingleClientConnection(Socket socket) {
    try {
      ServerPrinter.displayDebug("New connection from " + socket.getInetAddress() + ":" + socket.getPort());

      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      SocketClientHandler connection = new SocketClientHandler(out, in);

      Command command = (Command) in.readObject();

      if (command instanceof ConnectionCommand) {
        ConnectionCommand connectionCommand = (ConnectionCommand) command;
        String username = connectionCommand.username;

        ServerPrinter.displayDebug("Connection request from " + username);

        UserInfo userInfo = Server.INSTANCE.clientSignUp(username, connection);
        connections.put(userInfo, connection);
      } else if (command instanceof ReconnectionCommand) {
        ReconnectionCommand reconnectionCommand = (ReconnectionCommand) command;
        UserInfo userInfo = reconnectionCommand.userInfo;

        ServerPrinter.displayDebug("Reconnection request from " + userInfo);

        Server.INSTANCE.clientLogin(userInfo, connection);
        connections.put(userInfo, connection);
      } else {
        ServerPrinter.displayWarning("Invalid intial request");
      }

      executorService.submit(connection);
    } catch (Exception e) {
      e.printStackTrace();
      ServerPrinter.displayError("Error while accepting new client");
    }
  }

  /**
   * This method returns the connections map.
   * 
   * @return the connections map
   */
  public ConcurrentHashMap<UserInfo, SocketClientHandler> getConnections() {
    return connections;
  }
}
