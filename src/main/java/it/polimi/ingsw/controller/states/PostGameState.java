package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;

public class PostGameState extends GameState {
    public PostGameState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    @Override
    public boolean postGame() {
        // TODO check points and choose winner

        return false;
    }
}
