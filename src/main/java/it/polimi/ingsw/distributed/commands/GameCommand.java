package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;

public abstract class GameCommand {
    abstract void execute(GameFlowManager gameFlowManager);
}
