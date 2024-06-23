package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.Optional;

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
  private final Optional<Resources> nextCardSeed;

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
   * @param nextCardSeed the seed of the next card; it is an empty optional if
   *                     there is no next card
   * @param handIndex    the position (0,1,2) of the drawn card in the player hand
   */
  public DrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
      Optional<Resources> nextCardSeed, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardId = drawnCardId;
    this.deckEmptied = deckEmptied;
    this.nextCardSeed = nextCardSeed;
    this.handIndex = handIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnResourceDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardSeed, handIndex);
  }
}
