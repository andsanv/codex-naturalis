package it.polimi.ingsw.distributed.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.server.VirtualMainServer;

public class RMIMainView extends UnicastRemoteObject implements VirtualMainView, Runnable {
    private UserInfo userInfo;
    private VirtualMainServer virtualMainServer;

    // private final Object printLock;

    public RMIMainView() throws RemoteException {
        // this.printLock = printLock;
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        // synchronized (printLock) {
        System.err.println("Error: " + error);
        // }
    }

    @Override
    public void run() {
        Registry registry;
        try {
            // Connect to the server
            registry = LocateRegistry.getRegistry(Config.RMIServerPort);
            virtualMainServer = (VirtualMainServer) registry.lookup(Config.RMIServerName);

            // Send the virtual view to the server
            virtualMainServer.connect(this);

            // Create an user
            userInfo = virtualMainServer.signup("firstUser");

            System.out.println(userInfo);

            // Create a lobby
            LobbyInfo lobby = virtualMainServer.createLobby(userInfo);
            System.out.println(lobby);

            while (true) {
                Thread.sleep(1000);
            }

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error: failed to connect to the RMI server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public UserInfo getUserInfo() throws RemoteException {
        return userInfo;
    }

    @Override
    public void receiveLobbies(List<LobbyInfo> lobbies) throws RemoteException {
        System.out.println("Lobbies:");
        lobbies.stream().forEach(System.out::println);
    }
}
