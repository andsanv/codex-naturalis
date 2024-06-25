package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

import java.util.Optional;

/**
 * This event is used to notify that a card has been drawn from the gold cards'
 * deck.
 */
public final class DrawnGoldDeckCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int drawnCardId;
  private final boolean deckEmptied;
  private final Integer nextCardId;
  private final int handIndex;

  /**
   * This constructor creates the event starting from basic information.
   * 
   * @param playerToken  the token of the player who draws the card from the gold
   *                     deck.
   * @param drawnCardId  the drawn card id.
   * @param deckEmptied  a flag that is true if the deck is now empty, false
   *                     otherwise.
   * @param nextCardId   the id of the next card; null if there is no next card.
   * @param handIndex    the position (0,1,2) of the drawn card in the player hand.
   */
  public DrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
      Integer nextCardId, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardId = drawnCardId;
    this.deckEmptied = deckEmptied;
    this.nextCardId = nextCardId;
    this.handIndex = handIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnGoldDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardId, handIndex);
  }
}
