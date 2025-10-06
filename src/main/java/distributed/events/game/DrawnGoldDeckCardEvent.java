package distributed.events.game;

import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/**
 * This event is used to notify that a card has been drawn from the gold cards'
 * deck.
 */
public final class DrawnGoldDeckCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int drawnCardId;
  private final int deckSize;
  private final Integer nextCardId;
  private final int handIndex;

  /**
   * This constructor creates the event starting from basic information.
   * 
   * @param playerToken  the token of the player who draws the card from the gold
   *                     deck.
   * @param drawnCardId  the drawn card id.
   * @param deckSize     an integer that indicates the number of remaining cards in the deck.
   * @param nextCardId   the id of the next card; null if there is no next card.
   * @param handIndex    the position (0,1,2) of the drawn card in the player hand.
   */
  public DrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, int deckSize,
      Integer nextCardId, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardId = drawnCardId;
    this.deckSize = deckSize;
    this.nextCardId = nextCardId;
    this.handIndex = handIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnGoldDeckCardEvent(playerToken, drawnCardId, deckSize, nextCardId, handIndex);
  }
}
