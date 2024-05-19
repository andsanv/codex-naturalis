package it.polimi.ingsw.distributed.commands;

import java.io.Serializable;

import it.polimi.ingsw.controller.GameFlowManager;

/**
 * Every move by the clients is represented through a command, using the command pattern
 */

public abstract class GameCommand implements Serializable {
    public abstract boolean execute(GameFlowManager gameFlowManager);
}
