package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.PlayableCard;

import java.util.List;

/**
 * This class represents the three cards in a player's hand.
 */
public class PlayerHand {
    /**
     * The list that holds the cards in the player's hand.
     */
    private List<PlayableCard> cards;

    public PlayerHand(List<PlayableCard> cards) {
        this.cards = cards;
    }

    //getter
    public List<PlayableCard> getCards() {
        return cards;
    }
}
