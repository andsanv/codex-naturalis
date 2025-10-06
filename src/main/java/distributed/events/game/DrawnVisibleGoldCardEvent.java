package distributed.events.game;

import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/**
 * This event is used to notify that a card has been drawn from visible gold
 * cards.
 */
public final class DrawnVisibleGoldCardEvent extends GameEvent {

  /** The player token. */
  private final PlayerToken playerToken;

  /** The position of the drawn card. */
  private final int drawnCardPosition;

  /** The id of the drawn card. */
  private final int drawnCardId;

  /** The id of the replacement card. */
  private final Integer replacementCardId;

  /** This boolean field informs about the decks state. */
  private final int deckSize;

  /** The id of the next card. */
  private final Integer nextCardId;

  /** The position of the card in the player's hand. */
  private final int handIndex;

  /**
   * This constructor creates the event starting from basic information.
   * 
   * @param playerToken       the token of the player that draws the card.
   * @param drawnCardPosition the drawn card position in the visible resource
   *                          cards.
   * @param drawnCardId       the id of the drawn card.
   * @param replacementCardId the id of the replacement card.
   * @param deckSize          an integer that indicates the number of remaining cards in the deck.
   * @param nextCardId        id of the next card.
   * @param handIndex         index of the playerHand where drawn card will be
   *                          placed.
   */
  public DrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
      Integer replacementCardId, int deckSize, Integer nextCardId, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardPosition = drawnCardPosition;
    this.drawnCardId = drawnCardId;
    this.replacementCardId = replacementCardId;
    this.deckSize = deckSize;
    this.nextCardId = nextCardId;
    this.handIndex = handIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnVisibleGoldCardEvent(playerToken, drawnCardPosition, drawnCardId, replacementCardId,
        deckSize, nextCardId, handIndex);
  }
}
