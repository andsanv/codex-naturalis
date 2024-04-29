package it.polimi.ingsw.distributed.events;

import java.io.Serializable;

import it.polimi.ingsw.client.ClientCache;

public abstract class Event implements Serializable {
    public abstract void execute(ClientCache clientCache);
}
