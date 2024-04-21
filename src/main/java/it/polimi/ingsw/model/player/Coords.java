package it.polimi.ingsw.model.player;

import java.util.Objects;

/**
 * This class represents 2D coordinates that can be used to identify cards' position in the player board.
 *
 * @see PlayerBoard
 */
public final class Coords {
    public final int x;
    public final int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass())
            return false;

        return ((Coords) other).x == this.x && ((Coords) other).y == this.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
