package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.view.interfaces.MainEventHandler;

/**
 * This event is used to notify that the connection is still alive.
 */
public class KeepAliveEvent extends MainEvent {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(MainEventHandler mainEventHandler) {
    }
}
