package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.PlayableCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the three cards in a player's hand.
 */
public class PlayerHand {
    /**
     * The list that holds the cards in the player's hand.
     */
    private List<PlayableCard> cards;

    /**
     * Default constructor.
     * @param cards list of cards in player's hand.
     */
    public PlayerHand(List<PlayableCard> cards) {
        this.cards = cards;
    }

    /**
     * Constructor by copy.
     * @param other other PlayerHand object.
     */
    public PlayerHand(PlayerHand other) {
        this.cards = other.getCards();
    }

    /**
     * @param card card to add to the player's hand.
     */
    public void addCard(PlayableCard card) {
        cards.add(card);
    }

    /**
     * @param card card to remove from player's hand.
     */
    public void removeCard(PlayableCard card) {
        cards.remove(card);
    }

    /**
     * Method to get a list of cards.
     * @return list of cards.
     */
    public List<PlayableCard> getCards() {
        return new ArrayList<>(cards);
    }
}
