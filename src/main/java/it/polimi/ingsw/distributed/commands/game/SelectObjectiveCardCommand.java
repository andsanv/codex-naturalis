package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

public class SelectObjectiveCardCommand {
  private final PlayerToken playerToken;
  private final int choice;

  public SelectObjectiveCardCommand(PlayerToken playerToken, int choice) {
    this.playerToken = playerToken;
    this.choice = choice;
  }

  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.getCurrentState().selectObjectiveCard(playerToken, choice);
  }
}
