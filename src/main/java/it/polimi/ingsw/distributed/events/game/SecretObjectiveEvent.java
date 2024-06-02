package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a secret objective has been assigned to a player. */
public final class SecretObjectiveEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int secretObjectiveCardId;

  /**
   * @param playerToken the token of the player that gets the secret card
   * @param secretObjectiveCardId the secret card's id
   */
  public SecretObjectiveEvent(PlayerToken playerToken, int secretObjectiveCardId) {
    this.playerToken = playerToken;
    this.secretObjectiveCardId = secretObjectiveCardId;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handlePlayedCardEvent(playerToken, secretObjectiveCardId);
  }
}
