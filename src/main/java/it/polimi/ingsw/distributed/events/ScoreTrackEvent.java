package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal an update of a player's points.
 */
public final class ScoreTrackEvent extends GameEvent {
    private final UserInfo player;
    private final int score;

    /**
     * @param player the player who gets a new score
     * @param score  the new score
     */
    public ScoreTrackEvent(UserInfo player, int score) {
        this.player = player;
        this.score = score;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleScoreTrackEvent(player, score);
    }
}
