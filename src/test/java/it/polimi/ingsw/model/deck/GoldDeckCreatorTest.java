package it.polimi.ingsw.model.deck;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoldDeckCreatorTest {
  Deck<GoldCard> deck;

  @BeforeEach
  void setUp() throws IOException {
    deck = GoldDeckCreator.createDeck();
  }

  @Test
  void createDeck() {
    assertEquals(deck.size(), 40);

    for (int i = 0; i < 40; i++) {
      Optional<GoldCard> optionalGoldCard = deck.draw();
      if (optionalGoldCard.isPresent()) {
        GoldCard goldCard = optionalGoldCard.get();
        switch (goldCard.getId()) {
          case 43:
            assertTrue(goldCard.type.isPresent());
            assertEquals(goldCard.type.get(), Resources.FUNGI);
            assertEquals(goldCard.pointsType.name(), "ONE_PER_MANUSCRIPT");

            assertTrue(
                goldCard.getRequiredResources().containsKey(Resources.FUNGI)
                    && goldCard.getRequiredResources().get(Resources.FUNGI).equals(2));
            assertTrue(
                goldCard.getRequiredResources().containsKey(Resources.INSECT)
                    && goldCard.getRequiredResources().get(Resources.INSECT).equals(1));

            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.TOP_LEFT)
                    .equals(new Corner(Items.MANUSCRIPT, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.TOP_RIGHT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.BOTTOM_RIGHT)
                    .equals(new Corner(null, CornerTypes.HIDDEN)));
            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.BOTTOM_LEFT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));

            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.TOP_LEFT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.TOP_RIGHT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.BOTTOM_RIGHT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.BOTTOM_LEFT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));

            break;
          case 75:
            assertTrue(goldCard.type.isPresent());
            assertEquals(goldCard.type.get(), Resources.INSECT);
            assertEquals(goldCard.pointsType.name(), "TWO_PER_COVERED_CORNER");

            assertTrue(
                goldCard.getRequiredResources().containsKey(Resources.INSECT)
                    && goldCard.getRequiredResources().get(Resources.INSECT).equals(3));
            assertTrue(
                goldCard.getRequiredResources().containsKey(Resources.PLANT)
                    && goldCard.getRequiredResources().get(Resources.PLANT).equals(1));

            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.TOP_LEFT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.TOP_RIGHT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.BOTTOM_RIGHT)
                    .equals(new Corner(null, CornerTypes.HIDDEN)));
            assertTrue(
                goldCard.getFrontCorners()
                    .get(CornerPosition.BOTTOM_LEFT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));

            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.TOP_LEFT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.TOP_RIGHT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard
                    .getBackCorners()
                    .get(CornerPosition.BOTTOM_RIGHT)
                    .equals(new Corner(null, CornerTypes.VISIBLE)));
            assertTrue(
                goldCard
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
