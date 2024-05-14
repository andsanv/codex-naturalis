package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;

public abstract class GameCommand {
    public abstract boolean execute(GameFlowManager gameFlowManager);
}
