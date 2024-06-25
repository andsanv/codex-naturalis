package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

/** Thi event notify that a player has drawn his possible objective cards. */
public class DrawnObjectiveCardsEvent extends GameEvent {

  /** The token of the player drawing. */
  private final PlayerToken playerToken;

  /** The id of the first drawn card. */
  private final int firstDrawnCardId;

  /** The id of the second drawn card. */
  private final int secondDrawnCardId;

  /**
   * This constructor creates the event starting from the player token and the
   * cards id.
   * 
   * @param playerToken      token of the player drawing the cards.
   * @param firstDrawnCardId id of the objective card drawn.
   */
  public DrawnObjectiveCardsEvent(PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId) {
    this.playerToken = playerToken;
    this.firstDrawnCardId = firstDrawnCardId;
    this.secondDrawnCardId = secondDrawnCardId;
  }

  /**
   * {@inheritDoc}
   */
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDrawnObjectiveCardsEvent(playerToken, firstDrawnCardId, secondDrawnCardId);
  }
}
