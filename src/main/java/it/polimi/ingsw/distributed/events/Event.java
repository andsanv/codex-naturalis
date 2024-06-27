package it.polimi.ingsw.distributed.events;

import java.io.Serializable;

/**
 * This abstract class represents an event.
 * The destination of the events are the clients.
 * The events represent server actions or updates.
 * 
 * This class implements the Serializable interface to allow the events to be
 * sent over the network.
 */
public abstract class Event implements Serializable {
    
    /**
     * Default constructor.
     */
    public Event() {
    }
}
