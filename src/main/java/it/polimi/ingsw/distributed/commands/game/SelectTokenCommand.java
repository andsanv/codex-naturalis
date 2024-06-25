package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.player.PlayerToken;

/** This command is used to select the token */
public class SelectTokenCommand extends GameCommand {

  /** Player choosing the token. */
  public final UserInfo player;
  
  /** The chosen token */
  public final PlayerToken playerToken;

  /**
   * This constructor creates the command starting from the player and the selection.
   * 
   * @param player the player choosing.
   * @param playerToken the chosen token.
   */
  public SelectTokenCommand(UserInfo player, PlayerToken playerToken) {
    this.player = player;
    this.playerToken = playerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectToken(player, playerToken);
  }
}
