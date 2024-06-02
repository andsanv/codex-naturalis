package it.polimi.ingsw;

import it.polimi.ingsw.distributed.server.RMIMainServer;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

// Server entrypoint
public class ServerEntry {
  public static void main(String[] args) throws RemoteException, AlreadyBoundException {
    new Thread(new RMIMainServer()).start();
  }
}
