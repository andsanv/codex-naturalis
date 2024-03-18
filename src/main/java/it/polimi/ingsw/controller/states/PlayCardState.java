package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;

public class PlayCardState extends GameState {
    public PlayCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    public void dummy() {};

    public static class UpdateScoretrackState extends GameState {
        public UpdateScoretrackState(GameFlowManager gameFlowManager) {
            super(gameFlowManager);
        }

        public void dummy() {};
    }
}
