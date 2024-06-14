package it.polimi.ingsw.model.card;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayableCardTest {
  private PlayableCard card;

  private Map<CornerPosition, Corner> mockFrontCorners;

  private Map<CornerPosition, Corner> mockBackCorners;

  private Map<Resources, Integer> mockRequiredResources;

  @BeforeEach
  void init() {
    mockRequiredResources = new HashMap<>();
    mockFrontCorners = new HashMap<>();
    mockBackCorners = new HashMap<>();

    card =
        new GoldCard(
            1,
            Resources.ANIMAL,
            PointsType.ONE,
            mockRequiredResources,
            mockFrontCorners,
            mockBackCorners);
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
