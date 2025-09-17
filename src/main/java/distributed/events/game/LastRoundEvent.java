package distributed.events.game;

import view.interfaces.GameEventHandler;

/**
 * Event sent at the start of the last round of the game.
 */
public class LastRoundEvent extends GameEvent {
    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLastRoundEvent();
    }
}
