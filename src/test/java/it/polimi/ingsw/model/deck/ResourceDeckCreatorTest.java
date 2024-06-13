package it.polimi.ingsw.model.deck;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.player.PlayerToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourceDeckCreatorTest {

    Deck<ResourceCard> deck;

    List<Observer> obsList = new ArrayList<>();
    AtomicInteger atomicInt = new AtomicInteger(0);

    @BeforeEach
    void setUp() throws IOException {
        deck = ResourceDeckCreator.createDeck(obsList, atomicInt);
    }

    @Test
    void createDeck() {
        assertEquals(deck.size(), 40);

        for (int i = 0; i < 40; i++) {
            Optional<ResourceCard> optionalGoldCard = deck.draw(PlayerToken.RED, 0);
            if (optionalGoldCard.isPresent()) {
                ResourceCard resourceCard = optionalGoldCard.get();
                switch (resourceCard.id) {
                    case 6:
                        assertTrue(resourceCard.type.isPresent());
                        assertEquals(resourceCard.type.get(), Resources.FUNGI);
                        assertEquals(resourceCard.pointsType.name(), "ZERO");

                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.TOP_LEFT)
                                        .equals(new Corner(Items.INKWELL, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.TOP_RIGHT)
                                        .equals(new Corner(Resources.FUNGI, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.BOTTOM_RIGHT)
                                        .equals(new Corner(Resources.ANIMAL, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.BOTTOM_LEFT)
                                        .equals(new Corner(null, CornerTypes.HIDDEN)));

                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.TOP_LEFT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.TOP_RIGHT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.BOTTOM_RIGHT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.BOTTOM_LEFT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));

                        break;

                    case 39:
                        assertTrue(resourceCard.type.isPresent());
                        assertEquals(resourceCard.type.get(), Resources.INSECT);
                        assertEquals(resourceCard.pointsType.name(), "ONE");

                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.TOP_LEFT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.TOP_RIGHT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.BOTTOM_RIGHT)
                                        .equals(new Corner(Resources.INSECT, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getFrontCorners()
                                        .get(CornerPosition.BOTTOM_LEFT)
                                        .equals(new Corner(null, CornerTypes.HIDDEN)));

                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.TOP_LEFT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.TOP_RIGHT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.BOTTOM_RIGHT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));
                        assertTrue(
                                resourceCard
                                        .getBackCorners()
                                        .get(CornerPosition.BOTTOM_LEFT)
                                        .equals(new Corner(null, CornerTypes.VISIBLE)));

                        break;
                }
            }
        }

        assertEquals(deck.size(), 0);
    }
}
