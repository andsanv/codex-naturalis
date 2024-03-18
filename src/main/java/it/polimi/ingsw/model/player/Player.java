package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.ObjectiveCard;

/**
 * A player is the entity representing a client in a game.
 */
public class Player {
    /**
     * This attribute represents the player's board
     */
    private PlayerBoard board;
    /**
     * This attribute represents the player's hand
     */
    private PlayerHand hand;
    /**
     * This attribute represents the player's secret objective
     */
    public final ObjectiveCard secretObjective;

    /**
     * This is the constructor of a Player
     */
    Player() {
        //TODO to initialize a player secretobjective and board the startercardsdeck and  objective cardsdeck must be first initialized
        this.board = null;
        this.hand = null;
        this.secretObjective = null;
    }

    //GETTER
    public PlayerBoard getBoard() {
        // TODO choose how to return board (maybe through a copy)
        return board;
    }

    public PlayerHand getHand() {
        // TODO choose how to return hand (maybe through a copy)
        return hand;
    }
}