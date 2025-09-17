package distributed.commands.game;

import controller.GameFlowManager;
import model.player.PlayerToken;

/** This command is used to draw a resource card from the visible ones */
public class DrawVisibleResourceCardCommand extends GameCommand {

  /** Token of the player drawing the card. */
  private final PlayerToken playerToken;

  /** Index of the card chosen (0 or 1) */
  private final int choice;

  /**
   * This constructor creates the command starting from the player token and the selection.
   * 
   * @param playerToken the token of the player drawing.
   * @param choice the chosen card index, 0 or 1.
   */
  public DrawVisibleResourceCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    if(gameFlowManager.currentState.drawVisibleResourceCard(playerToken, choice)) {
      gameFlowManager.gameModelUpdater.computeCardsPlayability(playerToken);
      return true;
    }

    return false;
  }
}
