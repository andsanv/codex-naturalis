package it.polimi.ingsw.distributed;

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
import it.polimi.ingsw.distributed.actions.ServerActions;

public class RMIServer extends UnicastRemoteObject implements ServerActions, Runnable {
    public RMIServer() throws RemoteException {
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
    public List<LobbyInfo> getLobbies() throws RemoteException {
        return Server.INSTANCE.getLobbies();
    }

    @Override
    public boolean connectToGame(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.connectToGame(user, lobbyId);
    }

    @Override
    public boolean joinLobby(User user, int lobbyId) {
        return Server.INSTANCE.joinLobby(user, lobbyId);
    }

    @Override
    public boolean leaveLobby(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.leaveLobby(user, lobbyId);
    }

    @Override
    public UserInfo signup(String name, String password) throws RemoteException {
        return Server.INSTANCE.signup(name, password);
    }

    @Override
    public boolean startGame(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.startGame(user, lobbyId);
    }
}