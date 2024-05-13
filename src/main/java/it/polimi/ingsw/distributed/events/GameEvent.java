package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * A generic game event
 */
public abstract class GameEvent {
    /**
     * This method is called to handle a game event
     * @param gameUpdateHandler the event to handle
     */
    public abstract void execute(GameEventHandler gameUpdateHandler);
}
