package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoldCardTest {
    @Mock
    private Map<Elements, Integer> mockPlayerResources;

    @Mock
    private Map<Resources, Integer> mockRequiredResources;

    @Mock
    private Map<CornerPosition, Corner> mockFrontCorners;

    @Mock
    private Map<CornerPosition, Corner> mockBackCorners;

    private GoldCard card;

    @BeforeEach
    void init() {
        mockPlayerResources = mock(HashMap.class);
        mockRequiredResources = mock(HashMap.class);

        mockFrontCorners = mock(HashMap.class);
        mockBackCorners = mock(HashMap.class);

        when(mockPlayerResources.get(Resources.ANIMAL)).thenReturn(1);
        when(mockPlayerResources.get(Resources.PLANT)).thenReturn(2);
        when(mockPlayerResources.get(Resources.FUNGI)).thenReturn(3);
        when(mockPlayerResources.get(Resources.INSECT)).thenReturn(4);

        when(mockRequiredResources.get(Resources.ANIMAL)).thenReturn(5);
        when(mockRequiredResources.get(Resources.PLANT)).thenReturn(7);
        when(mockRequiredResources.get(Resources.FUNGI)).thenReturn(1);
        when(mockRequiredResources.get(Resources.INSECT)).thenReturn(3);

        card = new GoldCard(1, Resources.ANIMAL, PointsType.ONE, mockRequiredResources, mockFrontCorners, mockBackCorners);
    }

    @Test
    void enoughResourcesTest() {
        assertFalse(card.enoughResources(mockPlayerResources));

        when(mockPlayerResources.get(Resources.ANIMAL)).thenReturn(8);
        when(mockPlayerResources.get(Resources.PLANT)).thenReturn(7);
        when(mockPlayerResources.get(Resources.FUNGI)).thenReturn(8);
        when(mockPlayerResources.get(Resources.INSECT)).thenReturn(3);

        assertTrue(card.enoughResources(mockPlayerResources));
    }
}