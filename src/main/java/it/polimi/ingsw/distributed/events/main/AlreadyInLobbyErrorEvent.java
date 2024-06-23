package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

/**
 * This event is used to notify that the user is already in a lobby. This event
 * is sent in response to a create lobby command or a join lobby command if the
 * sending player is in a lobby.
 */
public class AlreadyInLobbyErrorEvent extends MainEvent {

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(MainEventHandler mainEventHandler) {
    mainEventHandler.handleAlreadyInLobbyErrorEvent();
  }
}
