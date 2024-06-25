package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.view.interfaces.MainEventHandler;

/**
 * Event sent upon receiving an invalid leave lobby command.
 * 
 * @see LeaveLobbyCommand
 */
public class LeaveLobbyError extends MainErrorEvent {
    /**
     * {@inheritDoc}
     */
    public LeaveLobbyError(String message) {
        super(message);
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleLeaveLobbyError(message);
    }
}
