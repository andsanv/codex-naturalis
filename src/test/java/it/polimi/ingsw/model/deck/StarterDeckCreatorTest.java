package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StarterDeckCreatorTest {

    Deck<StarterCard> deck;

    @BeforeEach
    void setUp() throws IOException {
        StarterDeckCreator starterDeckCreator = new StarterDeckCreator();
        deck = starterDeckCreator.createDeck();
    }

    @Test
    void createDeck() {
        assertEquals(deck.size(), 6);

        for(int i = 0; i < 40; i++) {
            Optional<StarterCard> optionalStarterCard = deck.draw();
            if(optionalStarterCard.isPresent()) {
                StarterCard starterCard = optionalStarterCard.get();
                switch (starterCard.getId()) {
                    case 0:
                        assertFalse(starterCard.getType().isPresent());

                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.TOP_LEFT), new Corner(null, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.TOP_RIGHT), new Corner(Resources.PLANT, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.BOTTOM_RIGHT), new Corner(null, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.BOTTOM_LEFT), new Corner(Resources.INSECT, CornerTypes.VISIBLE));

                        assertEquals(starterCard.getBackCorners().get(CornerPosition.TOP_LEFT), new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getBackCorners().get(CornerPosition.TOP_RIGHT), new Corner(Resources.PLANT, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getBackCorners().get(CornerPosition.BOTTOM_RIGHT), new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getBackCorners().get(CornerPosition.BOTTOM_LEFT), new Corner(Resources.INSECT, CornerTypes.VISIBLE));

                        assertTrue(starterCard.getCentralResources().contains(Resources.INSECT));
                        assertEquals(starterCard.getCentralResources().size(), 1);

                        break;


                    case 4:
                        assertFalse(starterCard.getType().isPresent());

                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.TOP_LEFT), new Corner(null, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.TOP_RIGHT), new Corner(null, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.BOTTOM_RIGHT), new Corner(null, CornerTypes.HIDDEN));
                        assertEquals(starterCard.getFrontCorners().get(CornerPosition.BOTTOM_LEFT), new Corner(null, CornerTypes.HIDDEN));

                        assertEquals(starterCard.getBackCorners().get(CornerPosition.TOP_LEFT), new Corner(Resources.INSECT, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getBackCorners().get(CornerPosition.TOP_RIGHT), new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getBackCorners().get(CornerPosition.BOTTOM_RIGHT), new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
                        assertEquals(starterCard.getBackCorners().get(CornerPosition.BOTTOM_LEFT), new Corner(Resources.PLANT, CornerTypes.VISIBLE));

                        assertTrue(starterCard.getCentralResources().contains(Resources.INSECT));
                        assertTrue(starterCard.getCentralResources().contains(Resources.ANIMAL));
                        assertTrue(starterCard.getCentralResources().contains(Resources.PLANT));
                        assertEquals(starterCard.getCentralResources().size(), 3);

                        break;

                }
            }
        }

        assertEquals(deck.size(), 0);
    }
}