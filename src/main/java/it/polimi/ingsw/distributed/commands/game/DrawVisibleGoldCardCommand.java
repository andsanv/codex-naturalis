package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw a gold card from the visible cards list.
 */
public class DrawVisibleGoldCardCommand extends GameCommand {
  /**
   * Token of the player drawing the card.
   */
  private final PlayerToken playerToken;

  /**
   * Index of the card chosen (0 or 1)
   */
  private final int choice;

  public DrawVisibleGoldCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    boolean result = gameFlowManager.currentState.drawVisibleGoldCard(playerToken, choice);
    gameFlowManager.addCommand(new CardsPlayabilityCommand(playerToken));

    return result;
  }
}
