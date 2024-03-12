package it.polimi.ingsw.model;

public class Player {
    private final PlayerBoard board;
    private final PlayerHand hand;
    private final ObjectiveCards secretObjective;

    Player() {
        this.board = null;
        this.hand = null;
        this.secretObjective = null;
    }
}