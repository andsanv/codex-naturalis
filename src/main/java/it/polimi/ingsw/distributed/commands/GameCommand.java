package it.polimi.ingsw.distributed.commands;

import java.io.Serializable;

import it.polimi.ingsw.controller.GameFlowManager;

public abstract class GameCommand implements Serializable {
    public abstract void execute(GameFlowManager gameFlowManager);
}
