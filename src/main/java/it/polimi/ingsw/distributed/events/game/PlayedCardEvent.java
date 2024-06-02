package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a card has been played. */
public final class PlayedCardEvent extends GameEvent {
  private final PlayerToken senderToken;
  private final int playedCardId;
  private final CardSide playedCardSide;
  private final Coords playedCardCoordinates;

  public PlayedCardEvent(
      PlayerToken senderToken,
      int playedCardId,
      CardSide playedCardSide,
      Coords playedCardCoordinates) {
    this.senderToken = senderToken;
    this.playedCardId = playedCardId;
    this.playedCardSide = playedCardSide;
    this.playedCardCoordinates = playedCardCoordinates;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handlePlayedCardEvent(
        senderToken, playedCardId, playedCardSide, playedCardCoordinates);
  }
}
