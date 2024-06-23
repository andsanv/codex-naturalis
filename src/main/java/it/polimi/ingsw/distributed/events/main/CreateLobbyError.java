package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

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
    mainEventHandler.handleCreateLobbyError();
  }
}
