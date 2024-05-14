package it.polimi.ingsw.distributed.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.VirtualMainView;

public class RMIMainServer extends UnicastRemoteObject implements VirtualMainServer, Runnable {
    public RMIMainServer() throws RemoteException {
    }

    @Override
    public void run() {
        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(Config.RMIServerPort);
            registry.bind(Config.RMIServerName, this);
        } catch (RemoteException e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.err.println("Error: " + Config.RMIServerName + " already bound");
            e.printStackTrace();
        }
    }

    @Override
    public boolean joinLobby(UserInfo user, int lobbyId) {
        return Server.INSTANCE.joinLobby(user, lobbyId);
    }

    @Override
    public boolean leaveLobby(UserInfo user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.leaveLobby(user, lobbyId);
    }

    @Override
    public UserInfo signup(String name) throws RemoteException {
        return Server.INSTANCE.signup(name);
    }

    @Override
    public boolean startGame(UserInfo user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.startGame(user, lobbyId);
    }

    @Override
    public void connect(VirtualMainView clientMainView) throws RemoteException {
        // TODO implement correctly
        Server.INSTANCE.addConnectedClient(clientMainView);
    }

    @Override
    public LobbyInfo createLobby(UserInfo userInfo) throws RemoteException {
        return Server.INSTANCE.createLobby(userInfo);
    }
}