package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.card.objective.ItemsObjectiveStrategy;
import it.polimi.ingsw.model.card.objective.ObjectiveStrategy;
import it.polimi.ingsw.model.card.objective.PatternObjectiveStrategy;
import it.polimi.ingsw.model.player.PlayerBoard;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ObjectiveCardTest {
    @Mock
    private PlayerBoard playerBoard;

    @Mock
    private ObjectiveStrategy patternObjectiveStrategy;

    @Mock
    private ObjectiveStrategy itemsObjectiveStrategy;

    private ObjectiveCard objectivePatternCard;
    private ObjectiveCard objectiveItemsCard;

    @BeforeEach
    void init() {
        playerBoard = mock(PlayerBoard.class);
        patternObjectiveStrategy = mock(PatternObjectiveStrategy.class);
        itemsObjectiveStrategy = mock(ItemsObjectiveStrategy.class);

        objectivePatternCard = new ObjectiveCard(1, 3, patternObjectiveStrategy);
        objectiveItemsCard = new ObjectiveCard(2,2,itemsObjectiveStrategy);

        when(patternObjectiveStrategy.getCompletedOccurrences(playerBoard)).thenReturn(4);
        when(itemsObjectiveStrategy.getCompletedOccurrences(playerBoard)).thenReturn(8);
    }

    @Test
    void computePointsTest() {
        assertEquals(12, objectivePatternCard.computePoints(playerBoard));
        assertEquals(16, objectiveItemsCard.computePoints(playerBoard));
    }
}