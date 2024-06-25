package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.view.interfaces.GameEventHandler;

/**
 * This class is used to notify about a general error in the game phases.
 */
public class GameErrorEvent extends GameEvent {
    /**
     * The error message.
     */
    protected final String message;

    /**
     * This constructor creates the event starting from the error message.
     * 
     * @param message the error message.
     */
    public GameErrorEvent(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGameError(message);
    }
}
