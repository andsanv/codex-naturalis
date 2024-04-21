package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.objective.ItemsObjectiveStrategy;
import it.polimi.ingsw.model.card.objective.PatternObjectiveStrategy;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ObjectiveDeckCreatorTest {

    Deck<ObjectiveCard> deck;

    @BeforeEach
    void setUp() throws IOException {
        deck = ObjectiveDeckCreator.createDeck();
    }

    @Test
    void createDeck() {
        assertEquals(16, deck.size());

        for(int i = 0; i < 40; i++) {
            Optional<ObjectiveCard> objectiveStarterCard = deck.draw();
            if(objectiveStarterCard.isPresent()) {
                ObjectiveCard objectiveCard = objectiveStarterCard.get();
                switch (objectiveCard.getId()) {
                    case 0:
                        assertEquals(objectiveCard.getPoints(), 2);

                        Map<Coords, Resources> mapping = new HashMap<>();
                        mapping.put(new Coords(0, 0), Resources.FUNGI);
                        mapping.put(new Coords(1, 1), Resources.FUNGI);
                        mapping.put(new Coords(2, 2), Resources.FUNGI);


                        ((PatternObjectiveStrategy) objectiveCard.getObjectiveStrategy())
                                .getPattern()
                                .keySet()
                                .forEach(e -> {
                                    assertTrue(mapping.containsKey(e));
                                    assertEquals(mapping.get(e),
                                            ((PatternObjectiveStrategy)objectiveCard.getObjectiveStrategy()).getPattern().get(e));
                                });

                        assertEquals(mapping.size(), ((PatternObjectiveStrategy)objectiveCard.getObjectiveStrategy()).getPattern().size());

                        break;

                    case 4:
                        assertEquals(objectiveCard.getPoints(), 3);

                        mapping = new HashMap<>();
                        mapping.put(new Coords(0, 1), Resources.FUNGI);
                        mapping.put(new Coords(0, 3), Resources.FUNGI);
                        mapping.put(new Coords(1, 0), Resources.PLANT);


                        ((PatternObjectiveStrategy) objectiveCard.getObjectiveStrategy())
                                .getPattern()
                                .keySet()
                                .forEach(e -> {
                                    assertTrue(mapping.containsKey(e));
                                    assertEquals(mapping.get(e),
                                            ((PatternObjectiveStrategy)objectiveCard.getObjectiveStrategy()).getPattern().get(e));
                                });

                        assertEquals(mapping.size(), ((PatternObjectiveStrategy)objectiveCard.getObjectiveStrategy()).getPattern().size());

                        break;

                    case 8:
                        assertEquals(objectiveCard.getPoints(), 2);

                        Map<Elements, Integer> requiredItems = new HashMap<>();
                        requiredItems.put(Resources.FUNGI, 3);


                        assertEquals(requiredItems.get(Resources.FUNGI),
                                ((ItemsObjectiveStrategy) objectiveCard.getObjectiveStrategy()).getRequiredItems().get(Resources.FUNGI));

                        assertEquals(requiredItems.size(), ((ItemsObjectiveStrategy) objectiveCard.getObjectiveStrategy()).getRequiredItems().size());

                        break;

                    case 12:
                        assertEquals(objectiveCard.getPoints(), 3);

                        requiredItems = new HashMap<>();
                        requiredItems.put(Items.INKWELL, 1);
                        requiredItems.put(Items.QUILL, 1);
                        requiredItems.put(Items.MANUSCRIPT, 1);


                        Map<Elements, Integer> map = ((ItemsObjectiveStrategy) objectiveCard.getObjectiveStrategy()).getRequiredItems();

                        assertTrue(requiredItems
                                .keySet()
                                .containsAll(map.keySet())
                        );

                        assertTrue(map
                                .keySet()
                                .containsAll(requiredItems.keySet()));

                        assertEquals(requiredItems.size(), map.size());

                        break;

                }
            }
        }

        assertEquals(deck.size(), 0);
    }
}