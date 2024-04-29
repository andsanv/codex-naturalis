package it.polimi.ingsw;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.RMIServer;

// Server entrypoint
public class ServerEntry {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        new Thread(new RMIServer()).start();
    }
}
