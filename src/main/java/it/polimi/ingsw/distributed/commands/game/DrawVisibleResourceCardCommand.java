package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw a resource card from the visible cards list
 */
public class DrawVisibleResourceCardCommand extends GameCommand {
  private final PlayerToken playerToken;
  private final int choice;

  public DrawVisibleResourceCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.getCurrentState().drawVisibleResourceCard(playerToken, choice);
  }
}
