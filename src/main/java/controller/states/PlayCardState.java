package controller.states;

import controller.GameFlowManager;
import model.card.CardSide;
import model.player.Coords;
import model.player.PlayerToken;

/**
 * The state of the game where a certain player needs to play a card
 */
public class PlayCardState extends GameState {
  public PlayCardState(GameFlowManager gameFlowManager) {
    super(gameFlowManager);
  }

  /**
   * Allows a player to play a card, on his board and at a certain position
   *
   * @param playerToken Token that represents the player
   * @param coords Coordinates where to play the card on the board
   * @param cardId id of the card to play
   * @param cardSide The side of the card to play
   * @return A boolean that depends on whether the operation was successful or not
   */
  @Override
  public boolean playCard(
      PlayerToken playerToken, Coords coords, int cardId, CardSide cardSide) {
    return gameFlowManager.getTurn().equals(playerToken)
        && gameModelUpdater.playCard(playerToken, coords, cardId, cardSide);
  }
}
