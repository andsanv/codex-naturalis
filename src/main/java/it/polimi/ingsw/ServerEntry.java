package it.polimi.ingsw;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.server.rmi.RMIMainServer;
import it.polimi.ingsw.distributed.server.socket.SocketServer;

// Server entrypoint
public class ServerEntry {
  public static void main(String[] args) throws AlreadyBoundException, IOException {
    socketTest();
  } 

  public void rmiTest() throws RemoteException {
    new Thread(new RMIMainServer()).start();
  }

  public static void socketTest() throws IOException {
    SocketServer server = new SocketServer(Config.MainSocketPort);
    
  }
}
