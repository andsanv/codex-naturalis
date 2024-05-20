package it.polimi.ingsw.distributed.events;

import java.io.Serializable;

import it.polimi.ingsw.distributed.MainEventHandler;

public abstract class ServerEvent implements Serializable {

    public abstract void execute(MainEventHandler mainEventHandler);
    
}
