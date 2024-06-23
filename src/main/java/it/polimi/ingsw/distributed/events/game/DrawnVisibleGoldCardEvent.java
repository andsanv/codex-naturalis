package it.polimi.ingsw.distributed.events.game;

import java.util.Optional;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;

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
  private final Optional<Integer> replacementCardId;

  /** This boolean field informs about the decks state. */
  private final boolean emptiedDeck;

  /** The seed of the next card. */
  private final Optional<Resources> nextCardSeed;

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
   * @param nextCardSeed      seed of the next card.
   * @param handIndex         index of the playerHand where drawn card will be
   *                          placed.
   */
  public DrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
      Optional<Integer> replacementCardId, boolean emptiedDeck, Optional<Resources> nextCardSeed, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardPosition = drawnCardPosition;
    this.drawnCardId = drawnCardId;
    this.replacementCardId = replacementCardId;
    this.emptiedDeck = emptiedDeck;
    this.nextCardSeed = nextCardSeed;
    this.handIndex = handIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnVisibleGoldCardEvent(playerToken, drawnCardPosition, drawnCardId, replacementCardId,
        emptiedDeck, nextCardSeed, handIndex);
  }
}
