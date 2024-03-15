package it.polimi.ingsw.model.player;

/**
 * This class represents coordinates in a 2D matrix and can be used to identify cards' position in the player board.
 * @see PlayerBoard
 */
final class Coords {
    public final int x;
    public final int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
