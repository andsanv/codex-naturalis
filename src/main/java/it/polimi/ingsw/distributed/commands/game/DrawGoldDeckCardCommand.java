package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * This command is used to draw a card from the gold deck.
 */
public class DrawGoldDeckCardCommand extends GameCommand {

  /** Token of the player drawing the card. */
  private final PlayerToken playerToken;

  /**
   * This constructor creates the command starting from the player token.
   * 
   * @param playerToken the token of the player drawing.
   */
  public DrawGoldDeckCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    if(gameFlowManager.currentState.drawGoldDeckCard(playerToken)) {
      gameFlowManager.gameModelUpdater.computeCardsPlayability(playerToken);
      return true;
    }

    return false;
  }
}
