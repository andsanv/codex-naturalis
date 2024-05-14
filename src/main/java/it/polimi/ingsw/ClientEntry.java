package it.polimi.ingsw;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.client.RMIMainView;
import it.polimi.ingsw.distributed.client.SocketMainView;
import it.polimi.ingsw.distributed.server.SocketMainServer;

// Client entrypoint
public class ClientEntry {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        // Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
        // VirtualMainServer serverActions = (VirtualMainServer) registry.lookup(Config.RMIServerName);

        // for (String s : registry.list())
        //     System.out.println(s);

        // serverActions.getLobbies().stream().forEach(System.out::println);

        // VirtualMainServer serverActions = new SocketMainServer(0);

        new Thread(new RMIMainView()).start();
    }

}