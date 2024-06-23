package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

/** This event is used to notify a main error. */
public class MainErrorEvent extends MainEvent {

  /** The error message. */
  private final String message;

  /**
   * This constructor creates the event starting from the error message.
   * 
   * @param message the error message.
   */
  public MainErrorEvent(String message) {
    this.message = message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(MainEventHandler mainEventHandler) {
    mainEventHandler.handleServerError(message);
  }
}
