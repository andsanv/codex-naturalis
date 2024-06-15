package it.polimi.ingsw;

import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.JoinLobbyCommand;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;
import it.polimi.ingsw.view.connection.SocketConnectionHandler;
import it.polimi.ingsw.view.tui.TUI;

import java.io.IOException;
import java.net.UnknownHostException;

// Client entrypoint
public class ClientEntry {
  // private final ConnectionHandler connectionHandler;

  public static void main(String[] args) throws Exception {
    
    TUI tui = new TUI();
    tui.start();

  }

  public static void rmiTest() throws Exception {

    CLITest clientEntry = new CLITest();

    RMIConnectionHandler connectionHandler = new RMIConnectionHandler(clientEntry);
    connectionHandler.sendToMainServer(new ConnectionCommand("rave"));

    while (clientEntry.getUserInfo() == null) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }

    System.out.println("RMI:" + clientEntry.getUserInfo());

    connectionHandler.sendToMainServer(new CreateLobbyCommand(clientEntry.getUserInfo()));

    new Thread(() -> {
      try {
        socketTest2(new CLITest());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

    
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
    }

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


