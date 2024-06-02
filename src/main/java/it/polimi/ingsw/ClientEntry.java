package it.polimi.ingsw;

import it.polimi.ingsw.client.ConnectionHandler;
import it.polimi.ingsw.client.RMIConnectionHandler;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

// Client entrypoint
public class ClientEntry {
  // private final ConnectionHandler connectionHandler;

  public static void main(String[] args) throws RemoteException, NotBoundException {
    ConnectionHandler connectionHandler = new RMIConnectionHandler();

    UserInfo userInfo = new UserInfo(new User("rave"));

    connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));

    CLITest clientEntry = new CLITest();

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
    }
  }
}
