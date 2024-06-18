package it.polimi.ingsw.model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CoordsTest {
    @Test
    void toStringTest() {
        Coords coords = new Coords(47,-5);
        
        assertEquals("(47,-5)", coords.toString());
    }

    @Test
    void equalsTest() {
        Coords coords = new Coords(47,-5);

        assertNotEquals(coords, null);
        assertNotEquals(coords, false);

        assertNotEquals(coords, new Coords(11, -5));
        assertNotEquals(coords, new Coords(47, 13));

        assertEquals(coords, new Coords(47, -5));
    }
}
