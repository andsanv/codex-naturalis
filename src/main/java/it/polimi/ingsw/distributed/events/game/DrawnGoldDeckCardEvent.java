package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.Optional;

/** Event to signal that a card has been drawn from the gold cards' deck. */
public final class DrawnGoldDeckCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int drawnCardId;
  private final boolean deckEmptied;
  private final Optional<Resources> nextCardSeed;

  /**
   * @param playerToken the token of the player who draws the card from the gold deck
   * @param drawnCardId the drawn card id
   */
  public DrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied, Optional<Resources> nextCardSeed) {
    this.playerToken = playerToken;
    this.drawnCardId = drawnCardId;
    this.deckEmptied = deckEmptied;
    this.nextCardSeed = nextCardSeed;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnGoldDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardSeed);
  }
}
