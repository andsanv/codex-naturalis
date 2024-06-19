package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw a gold card from the deck.
 */
public class DrawGoldDeckCardCommand extends GameCommand {
  /**
   * Token of the player drawing the card.
   */
  private final PlayerToken playerToken;

  public DrawGoldDeckCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    if(gameFlowManager.currentState.drawGoldDeckCard(playerToken)) {
      gameFlowManager.gameModelUpdater.computeCardsPlayability(playerToken);
      return true;
    }

    return false;
  }
}
