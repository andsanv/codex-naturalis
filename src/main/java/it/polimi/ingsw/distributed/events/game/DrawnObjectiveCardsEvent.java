package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a player has drawn his possible objective cards */
public class DrawnObjectiveCardsEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int firstCardId;
  private final int secondCardId;

  /**
   * @param playerToken token of the player drawing the cards
   * @param firstCardId id of the first objective card drawn
   * @param secondCardId id of the second objective card drawn
   */
  public DrawnObjectiveCardsEvent(PlayerToken playerToken, int firstCardId, int secondCardId) {
    this.playerToken = playerToken;
    this.firstCardId = firstCardId;
    this.secondCardId = secondCardId;
  }

  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnObjectiveCardsEvent(playerToken, firstCardId, secondCardId);
  }
}
