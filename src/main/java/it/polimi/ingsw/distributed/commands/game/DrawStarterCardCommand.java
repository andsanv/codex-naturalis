package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/** This command is used to draw a starter card */
public class DrawStarterCardCommand extends GameCommand {

  /** Token of the player drawing the card. */
  private PlayerToken playerToken;

  /**
   * This constructor creates the command starting from the player token.
   * 
   * @param playerToken the token of the player drawing.
   */
  public DrawStarterCardCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.drawStarterCard(playerToken);
  }
}
