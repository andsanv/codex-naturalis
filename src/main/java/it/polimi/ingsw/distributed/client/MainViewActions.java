package it.polimi.ingsw.distributed.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.main.MainEvent;

public interface MainViewActions extends Remote, Serializable {
    /**
     * Sends a server update to the client
     * 
     * @param serverEvent the event that the client will receive
     * @throws RemoteException
     */
    public void receiveEvent(MainEvent serverEvent) throws RemoteException;
}