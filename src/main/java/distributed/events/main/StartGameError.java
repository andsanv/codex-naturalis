package distributed.events.main;

import distributed.commands.main.StartGameCommand;
import view.interfaces.MainEventHandler;

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
