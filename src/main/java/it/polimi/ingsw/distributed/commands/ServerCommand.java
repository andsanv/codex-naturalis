package it.polimi.ingsw.distributed.commands;

import java.io.Serializable;

import it.polimi.ingsw.controller.server.Server;

public abstract class ServerCommand implements Serializable {
    
    /**
     * This method can only be called on the server, since it internally uses the Server singleton.
     */
    public abstract void execute();

}
