package it.polimi.ingsw.client;

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
    public void displayError(String error) throws RemoteException;

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
    public void setLobbies(List<LobbyInfo> lobbies) throws RemoteException;

    // TODO make lobbies updates incremental with the following methods

    /**
     * Adds a lobby to the current lobbies
     * 
     * @param lobby the new lobby
     * @throws RemoteException
     */
    public void addLobby(LobbyInfo lobby) throws RemoteException;

    /**
     * Removes a lobby with the give id
     * 
     * @param lobbyId the id of the lobby to delete
     * @throws RemoteException
     */
    public void deleteLobby(int lobbyId) throws RemoteException;

    /**
     * Updates an existing lobby
     * 
     * @param lobby the new lobby information
     * @throws RemoteException
     */
    public void updateLobby(LobbyInfo lobby) throws RemoteException;
}