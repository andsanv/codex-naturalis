package it.polimi.ingsw.controller.observer;

import it.polimi.ingsw.distributed.events.game.GameEvent;

/**
 * This class represents the observers in the Observer pattern.
 * Example of observers are the connected players.
 */
public interface Observer {
  /**
   * This method handles received GameEvents
   *
   * @param event the received event
   */
  void update(GameEvent event);
}
