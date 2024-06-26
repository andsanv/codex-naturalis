package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.view.interfaces.GameEventHandler;

/**
 * Event sent when the player is the last one connected to the game.
 */
public class LastConnectedPlayerEvent extends GameEvent {

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleLastConnectedPlayerEvent();
    }

}
