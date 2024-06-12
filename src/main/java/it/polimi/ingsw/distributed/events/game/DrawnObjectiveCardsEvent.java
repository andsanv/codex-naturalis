package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a player has drawn his possible objective cards */
public class DrawnObjectiveCardsEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int firstDrawnCardId;
  private final int secondDrawnCardId;

  /**
   * @param playerToken token of the player drawing the cards
   * @param firstDrawnCardId id of the objective card drawn
   */
  public DrawnObjectiveCardsEvent(PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId) {
    this.playerToken = playerToken;
    this.firstDrawnCardId = firstDrawnCardId;
    this.secondDrawnCardId = secondDrawnCardId;
  }

  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnObjectiveCardsEvent(playerToken, firstDrawnCardId, secondDrawnCardId);
  }
}
