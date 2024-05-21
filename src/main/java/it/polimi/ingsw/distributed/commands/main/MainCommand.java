package it.polimi.ingsw.distributed.commands.main;

import java.io.Serializable;

public abstract class MainCommand implements Serializable {
    /**
     * This method can only be called on the server, since it internally uses the
     * Server singleton.
     */
    public abstract void execute();

}
