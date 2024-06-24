package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** This event is used to notify an update of a player's points. */
public final class UpdatedScoreTrackEvent extends GameEvent {

  /**
   * The player's token.
   */
  public final PlayerToken playerToken;

  /**
   * The new score.
   */
  public final int score;

  /**
   * This constructor creates the event starting from the player token and the new
   * score.
   * 
   * @param playerToken the token of the player that gets a new score
   * @param score       the new score
   */
  public UpdatedScoreTrackEvent(PlayerToken playerToken, int score) {
    this.playerToken = playerToken;
    this.score = score;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleScoreTrackEvent(playerToken, score);
  }
}
