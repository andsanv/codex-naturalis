package it.polimi.ingsw.model.card;

/**
* Abstract class that represents a generic card, all cards must have a unique id.
*/
public abstract class Card {
    /**
     * Card's unique id.
     */
    private final int id;

    /**
     * @param id Unique id of the card.
     */
    public Card(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass())
            return false;

        return id == ((Card) other).id;
    }
}
