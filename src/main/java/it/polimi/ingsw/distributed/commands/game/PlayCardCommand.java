package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to play a card on his board.
 */
public class PlayCardCommand extends GameCommand {
  /**
   * Token of the player playing the card.
   */
  public final PlayerToken playerToken;

  /**
   * Coordinates where to play the card on his board.
   */
  public final Coords coords;

  /**
   * Id of the card to play.
   */
  public final int cardId;

  /**
   * Side of the card to play.
   */
  public final CardSide cardSide;

  public PlayCardCommand(
      PlayerToken playerToken, Coords coords, int cardId, CardSide cardSide) {
    this.playerToken = playerToken;
    this.coords = coords;
    this.cardId = cardId;
    this.cardSide = cardSide;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.playCard(playerToken, coords, cardId, cardSide);
  }
}
