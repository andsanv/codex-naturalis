package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.Trio;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

import java.util.List;

/** This event is used to notify about the results at the end of the game. */

public class GameResultsEvent extends GameEvent {
    /**
     * The representation of the results of the game. Contains each player token
     * with the final score and the number of completed objectives.
     */
    private final List<Trio<PlayerToken, Integer, Integer>> gameResults;

    /**
     * This constructor creates the event starting from the results of the game.
     * 
     * @param gameResults the results of the game.
     */
    public GameResultsEvent(List<Trio<PlayerToken, Integer, Integer>> gameResults) {
        this.gameResults = gameResults;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGameResultsEvent(gameResults);
    }
}
