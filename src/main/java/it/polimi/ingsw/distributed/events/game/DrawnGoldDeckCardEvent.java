package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.Optional;

/**
 * This event is used to notify that a card has been drawn from the gold cards'
 * deck.
 */
public final class DrawnGoldDeckCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int drawnCardId;
  private final boolean deckEmptied;
  private final Optional<Resources> nextCardSeed;
  private final int handIndex;

  /**
   * This constructor creates the event starting from basic information.
   * 
   * @param playerToken  the token of the player who draws the card from the gold
   *                     deck.
   * @param drawnCardId  the drawn card id.
   * @param deckEmptied  a flag that is true if the deck is now empty, false
   *                     otherwise.
   * @param nextCardSeed the seed of the next card; it is an empty optional if
   *                     there is no next card.
   * @param handIndex    the position (0,1,2) of the drawn card in the player hand.
   */
  public DrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
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
    gameUpdateHandler.handleDrawnGoldDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardSeed, handIndex);
  }
}
