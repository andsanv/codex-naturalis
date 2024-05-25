package it.polimi.ingsw.distributed.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.game.GameEvent;

public interface GameViewActions extends Remote {
    /**
     * Receives a game event.
     * 
     * @param event the sent event
     * @throws RemoteException
     */
    void receiveEvent(GameEvent event) throws RemoteException;
}
