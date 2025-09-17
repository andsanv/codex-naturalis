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
  private final boolean deckEmptied;

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
   * @param deckEmptied  a flag that is true if the deck is now empty, false
   *                     otherwise
   * @param nextCardId   the id of the next card; null if there is no next card.
   * @param handIndex    the position (0,1,2) of the drawn card in the player hand
   */
  public DrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
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
    gameUpdateHandler.handleDrawnResourceDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardId, handIndex);
  }
}
