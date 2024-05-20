package it.polimi.ingsw.distributed.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.GameEvent;

public interface GameViewActions extends Remote {
    /**
     * Receives a game event.
     * 
     * @param event the sent event
     * @throws RemoteException
     */
    void receiveEvent(GameEvent event) throws RemoteException;

    /**
     * Receives an error message.
     * This function is usually called when the client attemps an illegal move.
     * 
     * @param error error message to display
     * @throws RemoteException
     */
    void receiveError(String error) throws RemoteException;
}
