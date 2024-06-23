package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/** This command is used to draw a resource card. */
public class DrawResourceDeckCardCommand extends GameCommand {
  
  /** The token of the player drawing */
  private final PlayerToken playerToken;

  /**
   * This constructor creates the command starting from the player token.
   * 
   * @param playerToken
   */
  public DrawResourceDeckCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    if(gameFlowManager.currentState.drawResourceDeckCard(playerToken)) {
      gameFlowManager.gameModelUpdater.computeCardsPlayability(playerToken);
      return true;
    }
    
    return false;
  }
}
