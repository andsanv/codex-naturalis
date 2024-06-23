package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * THis event is used to notify that a starter card has been drawn by a player.
 */
public class DrawnStarterCardEvent extends GameEvent {

  /** The token of the player drawing. */
  private final PlayerToken playerToken;

  /** The id of the card drawn. */
  private final int cardId;

  /**
   * @param playerToken token of the player drawing the card.
   * @param cardId      id of the card drawn.
   */
  public DrawnStarterCardEvent(PlayerToken playerToken, int cardId) {
    this.playerToken = playerToken;
    this.cardId = cardId;
  }

  /**
   * {@inheritDoc}
   */
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnStarterCardEvent(playerToken, cardId);
  }
}
