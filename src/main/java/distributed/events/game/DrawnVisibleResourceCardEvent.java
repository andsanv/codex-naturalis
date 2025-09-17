package distributed.events.game;

import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/**
 * This event is used to notify that a card has been drawn from visible resource
 * cards.
 */
public final class DrawnVisibleResourceCardEvent extends GameEvent {

  /** The player's token. */
  private final PlayerToken playerToken;

  /** The position of the drawn card. */
  private final int drawnCardPosition;

  /** The id of the drawn card. */
  private final int drawnCardId;

  /** The id of the replacement card. */
  private final Integer replacementCardId;

  /** This boolean field informs about the decks state. */
  private final boolean emptiedDeck;

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
   * @param emptiedDeck       true if deck was emptied drawing the replacement
   *                          card, false otherwise.
   * @param nextCardId        id of the next card.
   * @param handIndex         index of the playerHand where drawn card will be
   *                          placed.
   */
  public DrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
      Integer replacementCardId, boolean emptiedDeck, Integer nextCardId, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardPosition = drawnCardPosition;
    this.drawnCardId = drawnCardId;
    this.replacementCardId = replacementCardId;
    this.emptiedDeck = emptiedDeck;
    this.nextCardId = nextCardId;
    this.handIndex = handIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnVisibleResourceCardEvent(playerToken, drawnCardPosition, drawnCardId,
        replacementCardId, emptiedDeck, nextCardId, handIndex);
  }
}
