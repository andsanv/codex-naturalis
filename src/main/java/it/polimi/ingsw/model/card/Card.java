package it.polimi.ingsw.model.card;

/**
* Abstract class that represents a generic card, all cards must have a unique id.
*/
public abstract class Card {
    private final int id;

    public Card(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
