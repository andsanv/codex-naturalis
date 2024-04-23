package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoldCardTest {
    private Map<Elements, Integer> playerResources;
    private Map<Resources, Integer> requiredResources;
    private Map<CornerPosition, Corner> frontCorners;
    private Map<CornerPosition, Corner> backCorners;

    private GoldCard card;

    @BeforeEach
    void init() {
        playerResources = new HashMap<>();
        requiredResources = new HashMap<>();

        frontCorners = new HashMap<>();
        backCorners = new HashMap<>();

        playerResources.put(Resources.ANIMAL, 1);
        playerResources.put(Resources.PLANT, 2);
        playerResources.put(Resources.FUNGI, 3);
        playerResources.put(Resources.INSECT, 4);

        requiredResources.put(Resources.ANIMAL, 5);
        requiredResources.put(Resources.PLANT, 7);
        requiredResources.put(Resources.FUNGI, 1);
        requiredResources.put(Resources.INSECT, 3);

        card = new GoldCard(1, Resources.ANIMAL, PointsType.ONE, requiredResources, frontCorners, backCorners);
    }

    @Test
    void enoughResourcesTest() {
        assertFalse(card.enoughResources(playerResources, CardSide.FRONT));

        playerResources.put(Resources.ANIMAL, 8);
        playerResources.put(Resources.PLANT, 7);
        playerResources.put(Resources.FUNGI, 8);
        playerResources.put(Resources.INSECT, 3);

        assertTrue(card.enoughResources(playerResources, CardSide.FRONT));
    }
}