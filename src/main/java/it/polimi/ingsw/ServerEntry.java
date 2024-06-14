package it.polimi.ingsw;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.server.rmi.RMIMainServer;
import it.polimi.ingsw.distributed.server.socket.SocketServer;

// Server entrypoint
public class ServerEntry {
  public static void main(String[] args) throws AlreadyBoundException, IOException {
    new Thread(() -> {
      try {
        socketTest();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

    rmiTest();
  } 

  public static void rmiTest() throws RemoteException {
    RMIMainServer rmiMainServer = new RMIMainServer();
    rmiMainServer.run();
  }

  public static void socketTest() throws IOException {
    SocketServer server = new SocketServer(Config.MainSocketPort);
  }
}
