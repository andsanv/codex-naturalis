package it.polimi.ingsw.distributed.actions;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.Event;

public interface ListenerActions {
    public Event getLastEvent() throws RemoteException;
}
