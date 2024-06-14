package it.polimi.ingsw;

import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.JoinLobbyCommand;
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
    new Thread(() -> {
      try {
        CLITest clientEntry = new CLITest();
        System.out.println(clientEntry.getUserInfo());
        socketTest(clientEntry);
      } catch (RemoteException e) {
        e.printStackTrace();
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }).start();
    
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
    }

    CLITest clientEntry2 = new CLITest();
    System.out.println("INIZIO " + clientEntry2.getUserInfo());
    socketTest2(clientEntry2);

  }

  public static void rmiTest() throws RemoteException {

    CLITest clientEntry = new CLITest();

    RMIConnectionHandler connectionHandler = new RMIConnectionHandler(clientEntry);  
  }

  public static void socketTest(CLITest cliTest) throws UnknownHostException, IOException {
    
    SocketConnectionHandler connectionHandler = new SocketConnectionHandler(cliTest);
    System.out.println("Sending connection command to server");

    connectionHandler.sendToMainServer(new ConnectionCommand("rave"));
    
    while (cliTest.getUserInfo() == null) {
      try {
        Thread.sleep(1000);
        } catch (InterruptedException e) {
      }
    }

    connectionHandler.sendToMainServer(new CreateLobbyCommand(cliTest.getUserInfo()));

    try {
      Thread.sleep(10000);
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

    try {
      Thread.sleep(20000);
    } catch (InterruptedException e) {
    }
    connectionHandler.close();
  }

  public static void socketTest2(CLITest cliTest2) throws UnknownHostException, IOException {
  
    System.out.println("How: " + cliTest2.getUserInfo());
    SocketConnectionHandler connectionHandler = new SocketConnectionHandler(cliTest2);
    System.out.println("!!!Sending connection command to server");

    connectionHandler.sendToMainServer(new ConnectionCommand("rave"));
    System.out.println("!!!Waiting for user info");
    System.out.println(cliTest2.getUserInfo());
    
    while (cliTest2.getUserInfo() == null) {
      try {
        Thread.sleep(1000);
        } catch (InterruptedException e) {
      }
    }

    System.out.println("!!!Sending join lobby command to server");
    System.out.println(cliTest2.getUserInfo());
    connectionHandler.sendToMainServer(new JoinLobbyCommand(cliTest2.getUserInfo(), 0));

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

    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
    }
    connectionHandler.close();
  }
}


