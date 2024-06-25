package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.view.interfaces.MainEventHandler;

/**
 * Event sent when there's an error after a start game command.
 * 
 * @see StartGameCommand
 */
public class StartGameError extends MainErrorEvent {
    /**
     * {@inheritDoc}
     */
    public StartGameError(String message) {
        super(message);
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleStartGameError(message);
    }
}
