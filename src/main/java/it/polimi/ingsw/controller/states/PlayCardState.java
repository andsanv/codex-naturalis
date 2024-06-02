package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

/** The state of the game where a certain player needs to play a card */
public class PlayCardState extends GameState {
  public PlayCardState(GameFlowManager gameFlowManager) {
    super(gameFlowManager);
  }

  /**
   * Allows a player to play a card on his board at a certain position
   *
   * @param playerToken Token that represents the player
   * @param coords Coordinates where to play the card on the board
   * @param card The card to play
   * @param cardSide The side of the card to play
   * @return A boolean that depends on whether the operation was successful or not
   */
  @Override
  public boolean playCard(
      PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
    return gameFlowManager.getTurn().equals(playerToken)
        && gameModelUpdater.playCard(playerToken, coords, card, cardSide);
  }
}
