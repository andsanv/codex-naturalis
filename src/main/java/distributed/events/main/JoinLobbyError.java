package distributed.events.main;

import distributed.commands.main.JoinLobbyCommand;
import view.interfaces.MainEventHandler;

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
