package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to choose his token during game setup phase.
 */
public class SelectTokenCommand extends GameCommand {
  /**
   * Player choosing the token.
   */
  public final UserInfo player;
  
  /**
   * The token chosen
   */
  public final PlayerToken playerToken;

  public SelectTokenCommand(UserInfo player, PlayerToken playerToken) {
    this.player = player;
    this.playerToken = playerToken;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectToken(player, playerToken);
  }
}
