package it.polimi.ingsw.distributed;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;


public interface ServerActions extends Remote {
    boolean joinLobby(User user, int lobbyId) throws RemoteException;

    boolean leaveLobby(User user, int lobbyId) throws RemoteException;

    List<LobbyInfo> getLobbies() throws RemoteException;

    boolean startGame(User user, int lobbyId) throws RemoteException;

    boolean connectToGame(User user, int lobbyId) throws RemoteException;

    UserInfo signup(String name, String password) throws RemoteException;
}
