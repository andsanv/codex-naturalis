package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.PlayableCard;

/**
 * A player is an entity representing one client in game.
 */
public class Player {
    /**
     * Attribute that represents the player's board.
     */
    private PlayerBoard playerBoard;

    /**
     * Attribute that represents the player's hand.
     */
    private PlayerHand hand;

    /**
     * Attribute that represents the player's secret objective.
     */
    public final ObjectiveCard secretObjective;

    /**
     * Constructor of a Player.
     */
    public Player(ObjectiveCard secretObjective) {
        // TODO to initialize a player secretObjective and board the starterCardsDeck and  objective cardsDeck must be first initialized
        this.hand = null;
        this.secretObjective = secretObjective;
    }

    /**
     * @return a copy of the player's board
     */
    public PlayerBoard getBoard() {
        return new PlayerBoard(playerBoard);
    }

    /**
     * @return a copy of the player's hand.
     */
    public PlayerHand getHand() {
        return new PlayerHand(hand);
    }

    /**
     * Method that plays the given card at the given coordinates
     *
     * @param coords coords at which to play the card
     * @param card   card to play
     * @return boolean based on whether the card was placed or not
     */
    public boolean playCard(Coords coords, PlayableCard card) {
        if (playerBoard.canPlaceCardAt(coords, card)) {
            playerBoard.setCard(coords, card);
            return true;
        }

        return false;
    }

}