package distributed.events.game;

import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/**
 * This event is used to notify about a player that chose its objective card.
 */
public class ChosenObjectiveCardEvent extends GameEvent {

  /** The player token that chose. */
  private final PlayerToken playerToken;

  /** The chosen card. */
  private final int cardId;

  /**
   * This constructor creates the event starting from the player token and the
   * card id.
   * 
   * @param playerToken token of the player that chose the objective card.
   * @param cardId      id of the objective card chosen.
   */
  public ChosenObjectiveCardEvent(PlayerToken playerToken, int cardId) {
    this.playerToken = playerToken;
    this.cardId = cardId;

  }

  /**
   * {@inheritDoc}
   */
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleChosenObjectiveCardEvent(playerToken, cardId);
  }
}
