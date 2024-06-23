package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;


/** This command is used to play a card */
public class PlayCardCommand extends GameCommand {

  /** Token of the player playing the card. */
  public final PlayerToken playerToken;

  /** The coordinates where to play the card on his board. */
  public final Coords coords;

  /** Id of the card to play. */
  public final int cardId;

  /** Side of the card to play. */
  public final CardSide cardSide;

  /**
   * This constructore creates the command starting from basic informations.
   * 
   * @param playerToken the token of the player playing.
   * @param coords the coordinates where to play the card.
   * @param cardId the id of the card to play.
   * @param cardSide the side of the card to play.
   */
  public PlayCardCommand(
      PlayerToken playerToken, Coords coords, int cardId, CardSide cardSide) {
    this.playerToken = playerToken;
    this.coords = coords;
    this.cardId = cardId;
    this.cardSide = cardSide;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.playCard(playerToken, coords, cardId, cardSide);
  }
}
