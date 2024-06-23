package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.List;

/** This event is used to notify about the results at the end of the game. */

public class GameResultsEvent extends GameEvent {
    /** The representation of the results of the game. */
    private final List<Pair<PlayerToken, Integer>> gameResults;

    /**
     * This constructor creates the event starting from the results of the game.
     * 
     * @param gameResults the results of the game.
     */
    public GameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
        this.gameResults = gameResults;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGameResultsEvent(gameResults);
    }
}
