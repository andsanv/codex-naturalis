package it.polimi.ingsw.distributed.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;

public interface VirtualMainView extends Remote, Serializable {
    /**
     * Sends an error to the client.
     * Usually sent after failing to do an action like joining a lobby or a game.
     * 
     * @param error error message to display
     * @throws RemoteException
     */
    public void sendError(String error) throws RemoteException;

    /**
     * Gives info on the connected client
     * 
     * @return client information as a UserInfo object
     * @throws RemoteException
     */
    public UserInfo getUserInfo() throws RemoteException;

    /**
     * Sends the current lobbies
     * 
     * @param lobbies a list of the existing lobbies
     * @throws RemoteException
     */
    public void receiveLobbies(List<LobbyInfo> lobbies) throws RemoteException;
}