package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;

/**
 * The class represents one client in game.
 */
public class Player {
  /** Attribute that represents the player's board. */
  private PlayerBoard playerBoard;

  /** Attribute that represents the player's hand. */
  private PlayerHand playerHand;

  /** Attribute that represents the player's secret objective. */
  public final ObjectiveCard secretObjective;

  /** Constructor of a Player. */
  public Player(StarterCard starterCard, CardSide starterCardSide, ObjectiveCard secretObjective) {
    // TODO to initialize a player secretObjective and board the starterCardsDeck and  objective
    // cardsDeck must be first initialized
    this.playerHand = new PlayerHand();
    this.secretObjective = secretObjective;
    this.playerBoard = new PlayerBoard(starterCard, starterCardSide);
  }

  /**
   * @return a copy of the player's board
   */
  public PlayerBoard getBoard() {
    return playerBoard;
  }

  /**
   * @return a copy of the player's hand.
   */
  public PlayerHand getHand() {
    return playerHand;
  }

  public void setPlayerHand(PlayerHand playerHand) {
    this.playerHand = playerHand;
  }

  /**
   * @param playerToken the token representing the player
   * @return common and secret objective points of the player
   */
  /* public int getObjectivesPoints() {
      PlayerBoard board = player.getBoard();

      return player.secretObjective.computePoints(board);
              + commonObjectives.stream().mapToInt(o -> o.computePoints(board)).sum();
  }*/
}
