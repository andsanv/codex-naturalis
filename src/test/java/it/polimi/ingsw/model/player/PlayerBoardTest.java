package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.text.Element;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayerBoardTest {
    PlayerBoard playerBoard;
    StarterCard starterCard;
    PlayableCard card1;
    GoldCard card2;

    @BeforeEach
    void setUp() {
        starterCard = createStarterCard0();
        playerBoard = new PlayerBoard(starterCard, CardSide.FRONT);

        card1 = createResCard0();
        card2 = createGoldCard0();
    }

    private StarterCard createStarterCard0() {
        int id = 0;
        Set<Resources> centralResources = new HashSet<>();
        centralResources.add(Resources.INSECT);

        Map<CornerPosition, Corner> frontCorners = new HashMap<>();
        frontCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.TOP_RIGHT, new Corner(Resources.PLANT, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(Resources.INSECT, CornerTypes.VISIBLE));


        Map<CornerPosition, Corner> backCorners = new HashMap<>();
        backCorners.put(CornerPosition.TOP_LEFT, new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.TOP_RIGHT, new Corner(Resources.PLANT, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(Resources.INSECT, CornerTypes.VISIBLE));

        return new StarterCard(id, centralResources, frontCorners, backCorners);
    }

    private ResourceCard createResCard0() {
        int id = 0;
        Resources type = Resources.FUNGI;
        PointsType resourceCardPoints = PointsType.ZERO;

        Map<CornerPosition, Corner> frontCorners = new HashMap<>();
        frontCorners.put(CornerPosition.TOP_LEFT, new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.HIDDEN));
        frontCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(Resources.FUNGI, CornerTypes.VISIBLE));


        Map<CornerPosition, Corner> backCorners = new HashMap<>();
        backCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

        return new ResourceCard(id, type, resourceCardPoints, frontCorners, backCorners);

    }

    private GoldCard createGoldCard0() {
        int id = 0;
        Resources type = Resources.FUNGI;
        PointsType goldCardPoints = PointsType.ONE_PER_QUILL;

        Map<Resources, Integer> requiredResources = new HashMap<>();
        requiredResources.put(Resources.FUNGI, 2);
        requiredResources.put(Resources.PLANT, 1);

        Map<CornerPosition, Corner> frontCorners = new HashMap<>();
        frontCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.HIDDEN));
        frontCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(Items.QUILL, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));


        Map<CornerPosition, Corner> backCorners = new HashMap<>();
        backCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

        return new GoldCard(id, type, goldCardPoints, requiredResources, frontCorners, backCorners);
    }

    //TODO: add more complex testing in different card mapping (e.g. allMatch(Corner::canPlaceCardAbove) angelo )
    @Test
    void canPlaceCardAtTestPosition() {
        // Test on starter card
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1, 1), card1, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1, -1), card1, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, 1), card1, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, -1), card1, CardSide.FRONT));

        assertTrue(playerBoard.canPlaceCardAt(new Coords(1, 1), card1, CardSide.BACK));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1, -1), card1, CardSide.BACK));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, 1), card1, CardSide.BACK));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, -1), card1, CardSide.BACK));

        assertFalse(playerBoard.canPlaceCardAt(new Coords(1, 0), card1, CardSide.FRONT));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(-1, 0), card1, CardSide.FRONT));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(0, -1), card1, CardSide.FRONT));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(0, 1), card1, CardSide.FRONT));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(0, 0), card1, CardSide.FRONT));

        // Test after card placement
        card1.playSide(CardSide.FRONT);
        playerBoard.setCard(new Coords(1, 1), card1);

        assertTrue(playerBoard.canPlaceCardAt(new Coords(2, 2), card2, CardSide.BACK));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(0, 2), card2, CardSide.BACK));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(1, 1), card2, CardSide.BACK));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, -1), card2, CardSide.BACK));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(2, 0), card2, CardSide.BACK));

        assertFalse(playerBoard.canPlaceCardAt(new Coords(-1, 0), card2, CardSide.BACK));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(0, -1), card2, CardSide.BACK));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(0, 1), card2, CardSide.BACK));
        assertFalse(playerBoard.canPlaceCardAt(new Coords(0, 0), card2, CardSide.BACK));
    }

    @Test
    void canPlaceCardAtTestResources() {
        playerBoard.getPlayerItems().put(Resources.FUNGI, 2);
        playerBoard.getPlayerItems().put(Resources.PLANT, 1);

        // Exactly enough resources to place
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.BACK));

        playerBoard.getPlayerItems().put(Resources.FUNGI, 6);
        playerBoard.getPlayerItems().put(Resources.PLANT, 4);

        // More than enough resources to place
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.BACK));

        playerBoard.getPlayerItems().put(Resources.FUNGI, 1);
        playerBoard.getPlayerItems().put(Resources.PLANT, 1);

        // One resource less, the other is enough to place
        assertFalse(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.BACK));

        playerBoard.getPlayerItems().put(Resources.FUNGI, 0);
        playerBoard.getPlayerItems().put(Resources.PLANT, 0);

        // Less than enough of both resources to place
        assertFalse(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.FRONT));
        assertTrue(playerBoard.canPlaceCardAt(new Coords(1,1), card2, CardSide.BACK));


    }
}