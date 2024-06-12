package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * Event to signal that a card has been drawn from visible gold cards.
 */
public final class DrawnVisibleGoldCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int drawnCardPosition;
  private final int drawnCardId;
  private final Optional<Integer> replacementCardId;
  private final boolean emptiedDeck;
  private final Optional<Resources> nextCardSeed;

  /**
   * @param playerToken the token of the player that draws the card
   * @param drawnCardPosition the drawn card position in the visible resource cards
   * @param drawnCardId the id of the drawn card
   * @param replacementCardId the id of the replacement card
   * @param emptiedDeck true if deck was emptied drawing the replacement card, false otherwise
   * @param nextCardSeed seed of the next card
   */
  public DrawnVisibleGoldCardEvent(
      PlayerToken playerToken,
      int drawnCardPosition,
      int drawnCardId,
      Optional<Integer> replacementCardId,
      boolean emptiedDeck,
      Optional<Resources> nextCardSeed
  ) {
    this.playerToken = playerToken;
    this.drawnCardPosition = drawnCardPosition;
    this.drawnCardId = drawnCardId;
    this.replacementCardId = replacementCardId;
    this.emptiedDeck = emptiedDeck;
    this.nextCardSeed = nextCardSeed;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnVisibleGoldCardEvent(
        playerToken,
        drawnCardPosition,
        drawnCardId,
        replacementCardId,
        emptiedDeck,
        nextCardSeed
    );
  }
}
