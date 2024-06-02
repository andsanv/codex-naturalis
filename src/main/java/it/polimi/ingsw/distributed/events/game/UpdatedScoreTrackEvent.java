package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal an update of a player's points. */
public final class UpdatedScoreTrackEvent extends GameEvent {
  private final PlayerToken playerToken;
  private final int score;

  /**
   * @param playerToken the token of the player that gets a new score
   * @param score the new score
   */
  public UpdatedScoreTrackEvent(PlayerToken playerToken, int score) {
    this.playerToken = playerToken;
    this.score = score;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleScoreTrackEvent(playerToken, score);
  }
}
