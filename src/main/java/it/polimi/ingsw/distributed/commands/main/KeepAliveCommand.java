package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.distributed.commands.Command;

public class KeepAliveCommand extends Command {
    public int lastKnownEventId;
}
