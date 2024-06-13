package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.Optional;

/** Event to signal that a card has been drawn from the resource cards' deck. */
public final class DrawnResourceDeckCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int drawnCardId;
  private final boolean deckEmptied;
  private final Optional<Resources> nextCardSeed;
  private final int handIndex;

  /**
   * @param playerToken the token of the player who draws the card from the resource deck
   * @param drawnCardId the drawn card id
   */
  public DrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex) {
    this.playerToken = playerToken;
    this.drawnCardId = drawnCardId;
    this.deckEmptied = deckEmptied;
    this.nextCardSeed = nextCardSeed;
    this.handIndex = handIndex;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnResourceDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardSeed, handIndex);
  }
}
