package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.gameflowmanager.gameflowmanager.states.*;
import it.polimi.ingsw.controller.gameflowmanager.manager.states.*;
import it.polimi.ingsw.controller.states.DrawCardState;
import it.polimi.ingsw.controller.states.GameState;
import it.polimi.ingsw.controller.states.PlayCardState;
import it.polimi.ingsw.controller.states.RefillCardsState;
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
        this.updateScoretrackState = new PlayCardState.UpdateScoretrackState(this);

        this.currentSate = this.playCardState;      //TODO
    }

    public void dummy() {
        currentSate.dummy();
    }


}
