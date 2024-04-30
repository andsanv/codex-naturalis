package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.GameModelUpdater;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.List;
import java.util.Map;

/**
 * A GameState refers to a state of the gameFlowManager (state machine), implemented using the state pattern
 * GameState is an abstract class, from which the actual state classes inherit the methods that will eventually be re-implemented
 */
public abstract class GameState {
    /**
     * The state machine
     */
    protected GameFlowManager gameFlowManager;

    protected GameModelUpdater gameModelUpdater;

    /**
     * The list of players in the game
     */
    protected List<String> playerIds;

    /**
     * A map from players' ids (a string) to their game token
     */
    protected Map<String, PlayerToken> IdToToken;

    /**
     * GameState constructor
     *
     * @param gameFlowManager the object containing the state machine
     */
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