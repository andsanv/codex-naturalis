package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.events.Event;


/**
 * A generic game event.
 */
public abstract class GameEvent extends Event {
    private int id = 0;

    /**
     * This method is called to handle a game event
     * @param gameUpdateHandler the event to handle
     */
    public abstract void execute(GameEventHandler gameUpdateHandler);

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
