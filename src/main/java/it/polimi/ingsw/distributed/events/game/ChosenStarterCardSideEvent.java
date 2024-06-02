package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a player has chosen the side of his starter card */
public class ChosenStarterCardSideEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final CardSide cardSide;

  /**
   * @param playerToken token of the player drawing the card
   * @param cardSide id of the card drawn
   */
  public ChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
    this.playerToken = playerToken;
    this.cardSide = cardSide;
  }

  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleChosenStarterCardSideEvent(playerToken, cardSide);
  }
}
