package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a player has chosen his private objective card */
public class ChosenObjectiveCardEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int cardId;

  /**
   * @param playerToken token of the player that chose the objective card
   * @param cardId id of the objective card chosen
   */
  public ChosenObjectiveCardEvent(PlayerToken playerToken, int cardId) {
    this.playerToken = playerToken;
    this.cardId = cardId;
  }

  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleChosenObjectiveCardEvent(playerToken, cardId);
  }
}
