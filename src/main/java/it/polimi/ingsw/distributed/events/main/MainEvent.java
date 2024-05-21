package it.polimi.ingsw.distributed.events.main;

import java.io.Serializable;

import it.polimi.ingsw.distributed.MainEventHandler;

public abstract class MainEvent implements Serializable {

    public abstract void execute(MainEventHandler mainEventHandler);
    
}
