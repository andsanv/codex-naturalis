package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.states.DrawCardState;
import it.polimi.ingsw.controller.states.*;
import it.polimi.ingsw.controller.states.PlayCardState;
import it.polimi.ingsw.controller.states.SetupState;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.player.PlayerHand;
import it.polimi.ingsw.model.player.PlayerToken;

import java.sql.Array;
import java.util.*;


//TODO: implement waiting room state in other controller (discuss)

public class GameFlowManager {
    public GameState setupState;
    public GameState playCardState;
    public GameState drawCardState;
    public GameState postGameState;

    public GameState currentState;

    public GameModelUpdater gameModelUpdater;

    public List<String> playersIds;
    public Map<String, PlayerToken> IdToToken;

    public Integer turn = 0;
    public Integer round = 0;
    public Boolean isLastRound = false;

    public GameFlowManager() {
        // this.playersIds = playersIds;
        this.IdToToken = new HashMap<String, PlayerToken>();
        this.gameModelUpdater = new GameModelUpdater(new GameModel());

        this.playCardState = new PlayCardState(this);
        this.drawCardState = new DrawCardState(this);
        this.setupState = new SetupState(this);
        this.postGameState = new PostGameState(this);

        this.currentState = this.setupState;
        currentState.setup();
    }

    public void setState(GameState nextState) {
        currentState = nextState;
    }

}




