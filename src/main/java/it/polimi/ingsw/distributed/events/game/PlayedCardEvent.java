package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

/** This event is used to notify that a card has been played by a player. */
public final class PlayedCardEvent extends GameEvent {
  public final PlayerToken senderToken;
  public final int playedCardId;
  public final CardSide playedCardSide;
  public final Coords playedCardCoordinates;

  /**
   * This constructor creates the event starting from the sender token, the played
   * card id, the side and coordinates.
   * 
   * @param senderToken           the token of the player that placed the card.
   * @param playedCardId          the id of the card played.
   * @param playedCardSide        the side of the card played.
   * @param playedCardCoordinates the coordinates where the card was placed.
   */
  public PlayedCardEvent(PlayerToken senderToken, int playedCardId, CardSide playedCardSide,
      Coords playedCardCoordinates) {
    this.senderToken = senderToken;
    this.playedCardId = playedCardId;
    this.playedCardSide = playedCardSide;
    this.playedCardCoordinates = playedCardCoordinates;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handlePlayedCardEvent(
        senderToken, playedCardId, playedCardSide, playedCardCoordinates);
  }
}
