package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw the two objective cards from the deck durin game setup phase.
 */
public class DrawObjectiveCardsCommand extends GameCommand {
  /**
   * Token of the player drawing the card.
   */
  private final PlayerToken playerToken;

  public DrawObjectiveCardsCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.drawObjectiveCards(playerToken);
  }
}
