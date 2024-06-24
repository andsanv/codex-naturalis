package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event sent at the start of the last round of the game.
 */
public class LastRoundEvent extends GameEvent {
    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLastRoundEvent();
    }
}
