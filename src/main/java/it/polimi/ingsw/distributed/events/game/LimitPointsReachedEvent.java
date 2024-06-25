package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

/** This event is used to notify about a player reaching the limit score. */
public class LimitPointsReachedEvent extends GameEvent {

    /** Token of the player who reached limitPoints points first. */
    private final PlayerToken playerToken;

    /** Score of the player who exceeded limitScore. */
    private final int score;

    /** Limit score to reach to enter last round. */
    private final int limitScore;

    /**
     * This constructor creates the event starting from the player token, the score
     * and the limit score.
     * 
     * @param playerToken the token of the player that reached the points.
     * @param score       the actual score of the player.
     * @param limitScore  the limit score to reach to enter the last round.
     */
    public LimitPointsReachedEvent(PlayerToken playerToken, int score, int limitScore) {
        this.playerToken = playerToken;
        this.score = score;
        this.limitScore = limitScore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLimitPointsReachedEvent(playerToken, score, limitScore);
    }
}
