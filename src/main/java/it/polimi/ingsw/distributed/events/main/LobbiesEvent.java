package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.view.interfaces.MainEventHandler;

import java.util.List;

/** This event is used to notify about the lobbies available. */
public class LobbiesEvent extends MainEvent {

  /** The listr of the lobbies */
  private final List<LobbyInfo> lobbies;

  /**
   * This constructor creates the event starting from the list of lobbies.
   * 
   * @param lobbies the list of lobbies.
   */
  public LobbiesEvent(List<LobbyInfo> lobbies) {
    this.lobbies = lobbies;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(MainEventHandler mainEventHandler) {
    mainEventHandler.handleLobbiesEvent(lobbies);
  }
}
