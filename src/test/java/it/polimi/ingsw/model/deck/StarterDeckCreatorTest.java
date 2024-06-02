package it.polimi.ingsw.model.deck;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StarterDeckCreatorTest {

  Deck<StarterCard> deck;

  @BeforeEach
  void setUp() throws IOException {
    deck = StarterDeckCreator.createDeck();
  }

  @Test
  void createDeck() {
    assertEquals(deck.size(), 6);

    for (int i = 0; i < 40; i++) {
      Optional<StarterCard> optionalStarterCard = deck.draw();
      if (optionalStarterCard.isPresent()) {
        StarterCard starterCard = optionalStarterCard.get();
        switch (starterCard.getId()) {
          case 81:
            assertFalse(starterCard.getType().isPresent());

            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.TOP_LEFT),
                new Corner(null, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.TOP_RIGHT),
                new Corner(Resources.PLANT, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.BOTTOM_RIGHT),
                new Corner(null, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.BOTTOM_LEFT),
                new Corner(Resources.INSECT, CornerTypes.VISIBLE));

            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.TOP_LEFT),
                new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.TOP_RIGHT),
                new Corner(Resources.PLANT, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.BOTTOM_RIGHT),
                new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.BOTTOM_LEFT),
                new Corner(Resources.INSECT, CornerTypes.VISIBLE));

            assertTrue(starterCard.getCentralResources().contains(Resources.INSECT));
            assertEquals(starterCard.getCentralResources().size(), 1);

            break;

          case 85:
            assertFalse(starterCard.getType().isPresent());

            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.TOP_LEFT),
                new Corner(null, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.TOP_RIGHT),
                new Corner(null, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.BOTTOM_RIGHT),
                new Corner(null, CornerTypes.HIDDEN));
            assertEquals(
                starterCard.getFrontCorners().get(CornerPosition.BOTTOM_LEFT),
                new Corner(null, CornerTypes.HIDDEN));

            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.TOP_LEFT),
                new Corner(Resources.INSECT, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.TOP_RIGHT),
                new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.BOTTOM_RIGHT),
                new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
            assertEquals(
                starterCard.getBackCorners().get(CornerPosition.BOTTOM_LEFT),
                new Corner(Resources.PLANT, CornerTypes.VISIBLE));

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
