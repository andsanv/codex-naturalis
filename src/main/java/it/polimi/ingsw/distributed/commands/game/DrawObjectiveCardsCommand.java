package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/** This command is used by a player to draw the two secrety initial objective cards. */
public class DrawObjectiveCardsCommand extends GameCommand {
  
  /** The token of the player drawing. */
  private final PlayerToken playerToken;

  /**
   * This constructor creates the command starting from the player token.
   * 
   * @param playerToken the token of the player drawing.
   */
  public DrawObjectiveCardsCommand(PlayerToken playerToken) {
    this.playerToken = playerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.drawObjectiveCards(playerToken);
  }
}
