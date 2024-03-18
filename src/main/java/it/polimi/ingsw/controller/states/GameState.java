package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;

public abstract class GameState {
    GameFlowManager gameFlowManager;

    GameState(GameFlowManager gameFlowManager){
        this.gameFlowManager = gameFlowManager;
    }

    public abstract void dummy();

    public void playCard() {
        // TODO ERROR
    }

    public void drawCard() {
        // TODO ERROR
    }
}