package it.polimi.ingsw.distributed.actions;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import it.polimi.ingsw.client.VirtualMainView;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;

// TODO replae user with userinfo (update methods in server to take UserInfo)
public interface VirtualMainServer extends Remote {
    boolean joinLobby(User user, int lobbyId) throws RemoteException;

    boolean leaveLobby(User user, int lobbyId) throws RemoteException;

    // TODO probably not needed
    List<LobbyInfo> getLobbies() throws RemoteException;

    boolean startGame(User user, int lobbyId) throws RemoteException;

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
