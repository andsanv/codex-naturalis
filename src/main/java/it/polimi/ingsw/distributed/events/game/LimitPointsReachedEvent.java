package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event that signals a player reaching the limit score.
 */
public class LimitPointsReachedEvent extends GameEvent {
    /**
     * Token of the player who reached limitPoints points first.
     */
    private final PlayerToken playerToken;

    /**
     * Score of the player who exceeded limitScore.
     */
    private final int score;

    /**
     * Limit score to reach to enter last round.
     */
    private final int limitScore;

    public LimitPointsReachedEvent(PlayerToken playerToken, int score, int limitScore) {
        this.playerToken = playerToken;
        this.score = score;
        this.limitScore = limitScore;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLimitPointsReachedEvent(playerToken, score, limitScore);
    }
}
