package it.polimi.ingsw;

import it.polimi.ingsw.client.ConnectionHandler;
import it.polimi.ingsw.client.RMIConnectionHandler;
import it.polimi.ingsw.client.SocketConnectionHandler;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

// Client entrypoint
public class ClientEntry {
  // private final ConnectionHandler connectionHandler;

  public static void main(String[] args) throws NotBoundException, UnknownHostException, IOException {
    socketTest();
  }

  public static void rmiTest() throws RemoteException {
    ConnectionHandler connectionHandler = new RMIConnectionHandler(new CLITest());

    UserInfo userInfo = new UserInfo(new User("rave"));

    connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));

    CLITest clientEntry = new CLITest();

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
    }
  }

  public static void socketTest() throws UnknownHostException, IOException {
    CLITest cliTest = new CLITest();
    ConnectionHandler connectionHandler = new SocketConnectionHandler(cliTest);
    System.out.println("Sending connection command to server");

    UserInfo userInfo = new UserInfo(new User("rave"));
    connectionHandler.sendToMainServer(new ConnectionCommand("rave"));
    connectionHandler.sendToMainServer(new ReconnectionCommand(userInfo));
    connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));

  }
}
