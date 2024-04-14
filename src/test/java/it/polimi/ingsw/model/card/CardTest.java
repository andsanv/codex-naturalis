package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class CardTest {
    private Card firstCard;
    private Card secondCard;

    @Mock
    private Map<Resources, Integer> mockRequiredResources;

    @Mock
    private Map<CornerPosition, Corner> mockFrontCorners;

    @Mock
    private Map<CornerPosition, Corner> mockBackCorners;

    @BeforeEach
    void init() {
        mockRequiredResources = mock(HashMap.class);
        mockFrontCorners = mock(HashMap.class);
        mockBackCorners = mock(HashMap.class);

        firstCard = new GoldCard(1, Resources.ANIMAL, GoldCardPoints.ONE, mockRequiredResources, mockFrontCorners, mockBackCorners);
    }

    @Test
    void equalsTest() {
        assertNotEquals(firstCard, null);

        secondCard = new GoldCard(2, Resources.FUNGI, GoldCardPoints.FIVE, mockRequiredResources, mockFrontCorners, mockBackCorners);
        assertNotEquals(firstCard, secondCard);

        secondCard = new GoldCard(1, Resources.ANIMAL, GoldCardPoints.ONE, mockRequiredResources, mockFrontCorners, mockBackCorners);
        assertEquals(firstCard, secondCard);

        secondCard = new ResourceCard(1, Resources.ANIMAL, ResourceCardPoints.ONE, mockFrontCorners, mockBackCorners);
        assertNotEquals(firstCard, secondCard);
    }
}
