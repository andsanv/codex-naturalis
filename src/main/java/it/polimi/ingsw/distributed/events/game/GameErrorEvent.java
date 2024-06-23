package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;

/** This class is used to notify about a general error in the game phases. */
public class GameErrorEvent extends GameEvent {

  /** The error message. */
  private final String message;

  /**
   * This constructor creates the event starting from the error message.
   * 
   * @param message the error message.
   */
  public GameErrorEvent(String message) {
    this.message = message;
  }

  /**
   * This getter method returns the message.
   * 
   * @return the error message.
   */
  public String getMessage() {
    return message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleGameError(message);
  }
}
