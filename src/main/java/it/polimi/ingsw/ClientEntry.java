package it.polimi.ingsw;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import it.polimi.ingsw.distributed.actions.VirtualMainServer;

// Client entrypoint
public class ClientEntry {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
        VirtualMainServer serverActions = (VirtualMainServer) registry.lookup(Config.RMIServerName);

        for (String s : registry.list())
            System.out.println(s);

        serverActions.getLobbies().stream().forEach(System.out::println);
    }

}