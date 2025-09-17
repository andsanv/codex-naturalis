package distributed.events.game;

import view.interfaces.GameEventHandler;

/**
 * Event sent when the player is the last one connected to the game.
 */
public class LastConnectedPlayerEvent extends GameEvent {

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLastConnectedPlayerEvent();
    }

}
