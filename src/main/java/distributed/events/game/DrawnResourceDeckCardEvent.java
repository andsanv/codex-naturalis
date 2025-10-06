package distributed.events.game;

import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/**
 * This event is used to notify that a card has been drawn from the resource
 * cards' deck.
 */
public final class DrawnResourceDeckCardEvent extends GameEvent {

  /** The token of the player drawing. */
  private final PlayerToken playerToken;

  /** The drawn card id. */
  private final int drawnCardId;

  /** This boolean field informs about the decks state. */
  private final int deckSize;

  /** The seed of the next card. */
  private final Integer nextCardId;

  /** The position of the drawn card in the player hand. */
  private final int handIndex;

  /**
   * This constructor creates the event starting from basic information.
   * 
   * @param playerToken  the token of the player who draws the card from the
   *                     resource deck
   * @param drawnCardId  the drawn card id
   * @param deckSize     an integer that indicates the number of remaining cards in the deck.
   * @param nextCardId   the id of the next card; null if there is no next card.
   * @param handIndex    the position (0,1,2) of the drawn card in the player hand
   */
  public DrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, int deckSize,
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
    gameUpdateHandler.handleDrawnResourceDeckCardEvent(playerToken, drawnCardId, deckSize, nextCardId, handIndex);
  }
}
