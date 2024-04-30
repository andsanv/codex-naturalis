package it.polimi.ingsw.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.actions.VirtualMainServer;

public class RMIMainView extends UnicastRemoteObject implements VirtualMainView, Runnable {
    private UserInfo userInfo;
    private VirtualMainServer virtualMainServer;

    // private final Object printLock;

    public RMIMainView() throws RemoteException {
        // this.printLock = printLock;
    }

    @Override
    public void displayError(String error) throws RemoteException {
        // synchronized (printLock) {
            System.err.println("Error: " + error);
        // }
    }

    @Override
    public void setLobbies(List<LobbyInfo> lobbies) throws RemoteException {
        // synchronized (printLock) {
            System.out.println("List of current lobbies: ");
            lobbies.forEach(System.out::println);
        // }
    }

    // TODO add incremental lobbies

    @Override
    public void addLobby(LobbyInfo lobby) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteLobby(int lobbyId) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateLobby(LobbyInfo lobby) throws RemoteException {
        // TODO Auto-generated method stub
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
}
