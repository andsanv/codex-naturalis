package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/** This command is used to draw gold cards from the visible ones. */
public class DrawVisibleGoldCardCommand extends GameCommand {
  
  /** Token of the player drawing the card. */
  private final PlayerToken playerToken;

  /** Index of the card chosen (0 or 1) */
  private final int choice;

  /**
   * This constructor creates the command starting from the player token and the selection.
   * @param playerToken the token of the player drawing.
   * @param choice the chosen card index, 0 or 1.
   */
  public DrawVisibleGoldCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    if(gameFlowManager.currentState.drawVisibleGoldCard(playerToken, choice)) {
      gameFlowManager.gameModelUpdater.computeCardsPlayability(playerToken);
      return true;
    }

    return false;
  }
}
