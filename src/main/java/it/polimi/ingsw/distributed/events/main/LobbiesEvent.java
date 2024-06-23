package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.distributed.MainEventHandler;
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
    System.out.println("Lobbies event created");
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
