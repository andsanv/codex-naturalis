package it.polimi.ingsw.distributed.commands.server;

import java.io.Serializable;

public abstract class ServerCommand implements Serializable {
    
    /**
     * This method can only be called on the server, since it internally uses the Server singleton.
     */
    public abstract void execute();

}
