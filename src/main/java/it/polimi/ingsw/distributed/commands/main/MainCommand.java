package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.distributed.commands.Command;

public abstract class MainCommand extends Command {
    /**
     * This method can only be called on the server, since it internally uses the
     * Server singleton.
     */
    public abstract void execute();

}
