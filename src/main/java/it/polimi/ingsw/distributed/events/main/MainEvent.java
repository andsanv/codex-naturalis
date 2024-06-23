package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.events.Event;

/**
 * THis abstract class represents a main event.
 * Main events are sent from the server to the clients while in the main menu.
 * They represent server actions or updates in the main menu.
 */
public abstract class MainEvent extends Event {

  /**
   * This method is executed on the client side, propagating the event to the client's main event handler.
   * @param mainEventHandler
   */
  public abstract void execute(MainEventHandler mainEventHandler);
}
