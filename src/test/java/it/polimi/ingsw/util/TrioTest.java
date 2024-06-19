package it.polimi.ingsw.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TrioTest {
    @Test
    void equalsTest() {
        Trio<Integer, Boolean, String> trio = new Trio<>(1, false, "test");
        Trio<Integer, Boolean, String> other = new Trio<>(1, false, "test");

        assertEquals(trio, trio);

        assertNotEquals(trio, null);
        assertNotEquals(trio, "test");

        assertEquals(trio, other);

        assertEquals(trio.hashCode(), trio.hashCode());

        assertNotEquals(trio, new Trio<Integer, Boolean, String>(0, false, "test"));
        assertNotEquals(trio, new Trio<Integer, Boolean, String>(1, true, "test"));
        assertNotEquals(trio, new Trio<Integer, Boolean, String>(1, false, "nontest"));
    }
}
