package it.polimi.ingsw.distributed.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.main.MainEvent;

public interface MainViewActions extends Remote, Serializable {
    /**
     * Sends an error to the client.
     * Usually sent after failing to do an action like joining a lobby or a game.
     * 
     * @param error error message to display
     * @throws RemoteException
     */
    public void receiveError(String error) throws RemoteException;

    /**
     * Sends a server update to the client
     * 
     * @param serverEvent the event that the client will receive
     * @throws RemoteException
     */
    public void receiveEvent(MainEvent serverEvent) throws RemoteException;
}