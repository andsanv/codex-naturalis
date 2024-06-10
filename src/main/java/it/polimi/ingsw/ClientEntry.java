package it.polimi.ingsw;

import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.view.connection.ConnectionHandler;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;
import it.polimi.ingsw.view.connection.SocketConnectionHandler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

// Client entrypoint
public class ClientEntry {
  // private final ConnectionHandler connectionHandler;

  public static void main(String[] args) throws NotBoundException, UnknownHostException, IOException {
    CLITest cliTest = new CLITest(new User("rave"));
    
    socketTest(cliTest);
  }

  public static void rmiTest() throws RemoteException {

    CLITest clientEntry = new CLITest(new User("rave"));

    ConnectionHandler connectionHandler = new RMIConnectionHandler(clientEntry);

    UserInfo userInfo = clientEntry.getUserInfo();

    connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));


    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
    }
  }

  public static void socketTest(CLITest cliTest) throws UnknownHostException, IOException {
    
    SocketConnectionHandler connectionHandler = new SocketConnectionHandler(cliTest);
    System.out.println("Sending connection command to server");

    connectionHandler.sendToMainServer(new ConnectionCommand("rave"));
    connectionHandler.sendToMainServer(new CreateLobbyCommand(cliTest.getUserInfo()));

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
    }

    connectionHandler.close();
    System.out.println("Connection closed");


    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
    }
    connectionHandler.reconnect();
    connectionHandler.sendToMainServer(new CreateLobbyCommand(cliTest.getUserInfo()));

    System.out.println("Reconnected");
    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
    }
  }
}
