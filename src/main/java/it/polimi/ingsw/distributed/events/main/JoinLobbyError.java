package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.view.interfaces.MainEventHandler;

/**
 * Event sent upon receiving an invalid join lobby command.
 * 
 * @see JoinLobbyCommand
 */
public class JoinLobbyError extends MainErrorEvent {
    /**
     * {@inheritDoc}
     */
    public JoinLobbyError(String message) {
        super(message);
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleJoinLobbyError(message);
    }
}
