package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to choose the side of the starter card
 */
public class SelectStarterCardSideCommand extends GameCommand {
  private final PlayerToken playerToken;
  private final CardSide cardSide;

  public SelectStarterCardSideCommand(
      GameFlowManager gameFlowManager, PlayerToken playerToken, CardSide cardSide) {
    this.playerToken = playerToken;
    this.cardSide = cardSide;
  }

  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectStarterCardSide(playerToken, cardSide);
  }
}
