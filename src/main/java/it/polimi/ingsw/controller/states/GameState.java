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

    public void dummy() {};

    public void setup() {}

    public void playCard(String playerId, Coords coords, PlayableCard card, CardSide cardSide) {
        // TODO ERROR
    }

    public void drawCard(String playerId, DrawChoices choice) {
        // TODO ERROR
    }
}