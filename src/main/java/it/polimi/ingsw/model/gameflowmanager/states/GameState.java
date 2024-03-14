package it.polimi.ingsw.model.gameflowmanager.states;

import it.polimi.ingsw.model.gameflowmanager.manager.GameFlowManager;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public abstract class GameState {
    GameFlowManager gameFlowManager;

    GameState(GameFlowManager gameFlowManager){
        this.gameFlowManager = gameFlowManager;
    }

    public abstract void dummy();
}