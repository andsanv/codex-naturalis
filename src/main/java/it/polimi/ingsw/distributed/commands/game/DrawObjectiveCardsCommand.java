package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw the two objective cards from the deck
 */
public class DrawObjectiveCardsCommand {
  private final PlayerToken playerToken;

  public DrawObjectiveCardsCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.drawObjectiveCards(playerToken);
  }
}
