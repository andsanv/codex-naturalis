package distributed.events.main;

import distributed.commands.main.CreateLobbyCommand;
import view.interfaces.MainEventHandler;

/**
 * Event send upon receiving an invalid create lobby command.
 * 
 * @see CreateLobbyCommand
 */
public class CreateLobbyError extends MainErrorEvent {
  public CreateLobbyError(String message) {
    super(message);
  }

  @Override
  public void execute(MainEventHandler mainEventHandler) {
    mainEventHandler.handleCreateLobbyError(message);
  }
}
