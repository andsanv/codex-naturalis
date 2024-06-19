package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to choose the side of the starter card during game setup phase.
 */
public class SelectStarterCardSideCommand extends GameCommand {
  /**
   * Token of the player choosing the side of the starter card.
   */
  private final PlayerToken playerToken;

  /**
   * The side chosen.
   */
  private final CardSide cardSide;

  public SelectStarterCardSideCommand(PlayerToken playerToken, CardSide cardSide) {
    this.playerToken = playerToken;
    this.cardSide = cardSide;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectStarterCardSide(playerToken, cardSide);
  }
}
