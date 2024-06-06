package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw a starter card from the deck
 */
public class DrawStarterCardCommand extends GameCommand {
  private PlayerToken playerToken;

  public DrawStarterCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.getCurrentState().drawStarterCard(playerToken);
  }
}
