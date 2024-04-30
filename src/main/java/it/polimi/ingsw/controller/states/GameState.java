package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.GameModelUpdater;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.List;
import java.util.Map;

public abstract class GameState {
    public GameFlowManager gameFlowManager;
    public GameModelUpdater gameModelUpdater;
    public List<String> playerIds;
    public Map<String, PlayerToken> IdToToken;


    GameState(GameFlowManager gameFlowManager){
        this.gameFlowManager = gameFlowManager;
        this.gameModelUpdater = gameFlowManager.gameModelUpdater;
        this.playerIds = gameFlowManager.playersIds;
        this.IdToToken = gameFlowManager.IdToToken;
    }


    public void setup() {}

    public boolean playCard(PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
        return false;
    }

    public boolean drawResourceDeckCard(PlayerToken playerToken) {
        return false;
    }

    public boolean drawGoldDeckCard(PlayerToken playerToken) {
        return false;
    }

    public boolean drawVisibleResourceCard(PlayerToken playerToken, int choice) {
        return false;
    }

    public boolean drawVisibleGoldCard(PlayerToken playerToken, int choice) {
        return false;
    }

    public boolean postGame() {
        return false;
    }
}