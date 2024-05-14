package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.VirtualMainView;

// TODO replace user with userinfo (update methods in server to take UserInfo)
public interface VirtualMainServer extends Remote {
    boolean joinLobby(UserInfo user, int lobbyId) throws RemoteException;

    boolean leaveLobby(UserInfo user, int lobbyId) throws RemoteException;

    boolean startGame(UserInfo user, int lobbyId) throws RemoteException;

    UserInfo signup(String name) throws RemoteException;

    LobbyInfo createLobby(UserInfo userInfo) throws RemoteException;

    /**
     * This method must be called when connecting to the server.
     * 
     * @param clientMainView the client's main view
     * @throws RemoteException
     */
    void connect(VirtualMainView clientMainView) throws RemoteException;
}
