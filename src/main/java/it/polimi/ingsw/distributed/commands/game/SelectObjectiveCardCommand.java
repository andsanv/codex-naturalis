package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/** Thi command is used to let a player choose its objective card in the game setup phase */
public class SelectObjectiveCardCommand extends GameCommand {

  /** Token of the player choosing the objective card. */
  private final PlayerToken playerToken;

  /** Index of the objective card chosen (0 or 1) */
  private final int choice;

  /**
   * This constructor creates the command starting from the player token and the selection.
   * 
   * @param playerToken the token of the player choosing.
   * @param choice the chosen card index, 0 or 1.
   */
  public SelectObjectiveCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectObjectiveCard(playerToken, choice);
  }
}
