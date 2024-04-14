package it.polimi.ingsw.model.corner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;

public class CornerTest {
    @Test
    void testCanPlaceCardAbove() {
        Corner corner;

        corner = new Corner(Resources.FUNGI, CornerTypes.VISIBLE);
        assertTrue(corner.canPlaceCardAbove());

        corner = new Corner(Items.INKWELL, CornerTypes.HIDDEN);
        assertFalse(corner.canPlaceCardAbove());

        corner = new Corner(Resources.ANIMAL, CornerTypes.COVERED);
        assertFalse(corner.canPlaceCardAbove());
    }

    @Test
    void testEquals() {
        Corner corner1;
        Corner corner2;

        corner1 = new Corner(Resources.ANIMAL, CornerTypes.VISIBLE);
        corner2 = new Corner(Resources.ANIMAL, CornerTypes.VISIBLE);
        assertEquals(corner1, corner2);

        corner1 = new Corner(null, CornerTypes.VISIBLE);
        corner2 = new Corner(null, CornerTypes.VISIBLE);
        assertEquals(corner1, corner2);

        corner1 = new Corner(Items.MANUSCRIPT, CornerTypes.HIDDEN);
        corner2 = new Corner(Items.MANUSCRIPT, CornerTypes.HIDDEN);
        assertEquals(corner1, corner2);

        corner1 = new Corner(Resources.PLANT, CornerTypes.COVERED);
        corner2 = new Corner(Resources.PLANT, CornerTypes.COVERED);
        assertEquals(corner1, corner2);
    }

    @Test
    void testGetItem() {
        Corner corner;

        corner = new Corner(Resources.INSECT, CornerTypes.VISIBLE);
        assertEquals(Optional.of(Resources.INSECT), corner.getItem());

        corner = new Corner(Resources.PLANT, CornerTypes.COVERED); 
        assertEquals(Optional.of(Resources.PLANT), corner.getItem());

        corner = new Corner(null, CornerTypes.HIDDEN); 
        assertEquals(Optional.empty(), corner.getItem());
    }

    @Test
    void testGetType() {
        Corner corner;

        corner = new Corner(null, CornerTypes.VISIBLE);
        assertEquals(CornerTypes.VISIBLE, corner.getType());

        corner = new Corner(Resources.FUNGI, CornerTypes.COVERED); 
        assertEquals(CornerTypes.COVERED, corner.getType());

        corner = new Corner(null, CornerTypes.HIDDEN); 
        assertEquals(CornerTypes.HIDDEN, corner.getType());
    }
}
