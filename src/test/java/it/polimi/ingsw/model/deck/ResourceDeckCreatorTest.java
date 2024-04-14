package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDeckCreatorTest {

    Deck<ResourceCard> deck;

    @BeforeEach
    void setUp() throws IOException {
        ResourceDeckCreator resourceDeckCreator = new ResourceDeckCreator();
        deck = resourceDeckCreator.createDeck();
    }

    @Test
    void createDeck() {
        assertEquals(deck.size(), 40);

        for(int i = 0; i < 40; i++) {
            Optional<ResourceCard> optionalGoldCard = deck.draw();
            if(optionalGoldCard.isPresent()) {
                ResourceCard resourceCard = optionalGoldCard.get();
                switch (resourceCard.getId()) {
                    case 5:
                        assertTrue(resourceCard.getType().isPresent());
                        assertEquals(resourceCard.getType().get(), Resources.FUNGI);
                        assertEquals(resourceCard.getPoints().name(), "ZERO");

                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.TOP_LEFT).equals(new Corner(Items.INKWELL, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.TOP_RIGHT).equals(new Corner(Resources.FUNGI, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.BOTTOM_RIGHT).equals(new Corner(Resources.ANIMAL, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.BOTTOM_LEFT).equals(new Corner(null, CornerTypes.HIDDEN)));

                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.TOP_LEFT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.TOP_RIGHT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.BOTTOM_RIGHT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.BOTTOM_LEFT).equals(new Corner(null, CornerTypes.VISIBLE)));

                        break;

                    case 38:
                        assertTrue(resourceCard.getType().isPresent());
                        assertEquals(resourceCard.getType().get(), Resources.INSECT);
                        assertEquals(resourceCard.getPoints().name(), "ONE");

                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.TOP_LEFT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.TOP_RIGHT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.BOTTOM_RIGHT).equals(new Corner(Resources.INSECT, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getFrontCorners().get(CornerPosition.BOTTOM_LEFT).equals(new Corner(null, CornerTypes.HIDDEN)));

                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.TOP_LEFT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.TOP_RIGHT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.BOTTOM_RIGHT).equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(resourceCard.getBackCorners().get(CornerPosition.BOTTOM_LEFT).equals(new Corner(null, CornerTypes.VISIBLE)));

                        break;
                }
            }
        }


        assertEquals(deck.size(), 0);
    }
}