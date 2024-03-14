package it.polimi.ingsw.model.gameflowmanager.manager;

import it.polimi.ingsw.model.gameflowmanager.states.*;

public class GameFlowManager {
    GameState playCardState;
    GameState drawCardState;
    GameState refillCardsState;
    GameState updateScoretrackState;

    private GameState currentSate;

    GameFlowManager() {
        this.playCardState = new PlayCardState(this);
        this.drawCardState = new DrawCardState(this);
        this.refillCardsState = new RefillCardsState(this);
        this.updateScoretrackState = new UpdateScoretrackState(this);

        this.currentSate = this.playCardState;      //TODO
    }

    public void dummy() {
        currentSate.dummy();
    }
}
