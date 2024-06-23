package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.events.Event;

/**
 * This abstract class represents a game event.
 * Game events are sent from the server to the clients during the game phases.
 * They represent server actions or updates in the game.
 */
public abstract class GameEvent extends Event {

  /** The id assigned to the game event */
  private int id = 0;

  /**
   * This method is called to handle a game event.
   *
   * @param gameUpdateHandler the event handler.
   */
  public abstract void execute(GameEventHandler gameUpdateHandler);

  /** This getter method is used by subclasses to get the game event id */
  public int getId() {
    return id;
  }

  /** This setter method is used by subclasses to set the game event id */
  public void setId(int id) {
    this.id = id;
  }
}
