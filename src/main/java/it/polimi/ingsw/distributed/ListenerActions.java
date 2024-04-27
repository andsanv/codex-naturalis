package it.polimi.ingsw.distributed;

import java.rmi.Remote;

import it.polimi.ingsw.distributed.events.Event;

public interface ListenerActions extends Remote {
    public Event getLastEvent();

        
}
