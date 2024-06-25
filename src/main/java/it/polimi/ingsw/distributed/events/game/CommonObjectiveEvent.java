package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.view.interfaces.GameEventHandler;

/**
 * This event is used to notify the clients about the two chosen common
 * objective cards.
 */
public final class CommonObjectiveEvent extends GameEvent {

  /** The first objective card id. */
  private final int firstCommonObjectiveId;

  /** The second objective card id. */
  private final int secondCommonObjectiveId;

  /**
   * This constructor creates the event starting from the two common objective
   * card ids.
   * 
   * @param firstCommonObjectiveId  id of the first common objective card.
   * @param secondCommonObjectiveId id of the second common objective card.
   */
  public CommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
    this.firstCommonObjectiveId = firstCommonObjectiveId;
    this.secondCommonObjectiveId = secondCommonObjectiveId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleCommonObjectiveEvent(firstCommonObjectiveId, secondCommonObjectiveId);
  }
}
