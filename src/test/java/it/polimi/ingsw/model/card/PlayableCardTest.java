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
import static org.mockito.Mockito.mock;

class PlayableCardTest {
    private PlayableCard card;

    @Mock
    private Map<CornerPosition, Corner> mockFrontCorners;

    @Mock
    private Map<CornerPosition, Corner> mockBackCorners;

    @Mock
    private Map<Resources, Integer> mockRequiredResources;

    @BeforeEach
    void init() {
        mockRequiredResources = mock(HashMap.class);
        mockFrontCorners = mock(HashMap.class);
        mockBackCorners = mock(HashMap.class);

        card = new GoldCard(1, Resources.ANIMAL, PointsType.ONE, mockRequiredResources, mockFrontCorners, mockBackCorners);
    }

    @Test
    void playSideTest() {
        CardSide side = CardSide.FRONT;
        card.playSide(side);

        assertEquals(card.getPlayedSide(), side);
        assertEquals(card.getActiveCorners(), mockFrontCorners);

        side = CardSide.BACK;
        card.playSide(side);
        assertEquals(card.getPlayedSide(), side);
        assertEquals(card.getActiveCorners(), mockBackCorners);
    }
}
