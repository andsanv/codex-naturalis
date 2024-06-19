package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to choose his objective card during game setup phase.
 */
public class SelectObjectiveCardCommand extends GameCommand {
  /**
   * Token of the player choosing the objective card.
   */
  private final PlayerToken playerToken;

  /**
   * Index of the objective card chosen (0 or 1)
   */
  private final int choice;

  public SelectObjectiveCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectObjectiveCard(playerToken, choice);
  }
}
