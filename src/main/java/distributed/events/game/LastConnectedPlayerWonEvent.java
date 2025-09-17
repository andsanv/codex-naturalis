package distributed.events.game;

import view.interfaces.GameEventHandler;

/**
 * Event sent when the last connected player is the winner of the game.
 */
public class LastConnectedPlayerWonEvent extends GameEvent {
    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLastConnectedPlayerWonEvent();
    }
}
