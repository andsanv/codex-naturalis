package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to draw a gold card from the deck
 */
public class DrawGoldDeckCardCommand extends GameCommand {
  private final PlayerToken playerToken;

  public DrawGoldDeckCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    boolean result = gameFlowManager.getCurrentState().drawGoldDeckCard(playerToken);
    gameFlowManager.addCommand(new CardsPlayabilityCommand(playerToken));

    return result;
  }
}
