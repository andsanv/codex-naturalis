package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw a resource card from the deck
 */
public class DrawResourceDeckCardCommand extends GameCommand {
  private final PlayerToken playerToken;

  public DrawResourceDeckCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    boolean result = gameFlowManager.currentState.drawResourceDeckCard(playerToken);
    gameFlowManager.addCommand(new CardsPlayabilityCommand(playerToken));

    return result;
  }
}
