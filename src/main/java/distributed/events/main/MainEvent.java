package distributed.events.main;

import distributed.events.Event;
import view.interfaces.MainEventHandler;

/**
 * THis abstract class represents a main event.
 * Main events are sent from the server to the clients while in the main menu.
 * They represent server actions or updates in the main menu.
 */
public abstract class MainEvent extends Event {

  /**
   * This method is executed on the client side, propagating the event to the client's main event handler.
   * 
   * @param mainEventHandler the event handler.
   */
  public abstract void execute(MainEventHandler mainEventHandler);
}
