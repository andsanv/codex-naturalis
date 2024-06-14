package it.polimi.ingsw.model.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class CardTest {
  private Card firstCard;
  private Card secondCard;

  @Mock private Map<Resources, Integer> mockRequiredResources;

  @Mock private Map<CornerPosition, Corner> mockFrontCorners;

  @Mock private Map<CornerPosition, Corner> mockBackCorners;

  @BeforeEach
  void init() {
    mockRequiredResources = new HashMap<>();
    mockFrontCorners = new HashMap<>();
    mockBackCorners = new HashMap<>();

    firstCard =
        new GoldCard(
            1,
            Resources.ANIMAL,
            PointsType.ONE,
            mockRequiredResources,
            mockFrontCorners,
            mockBackCorners);
  }

  @Test
  void equalsTest() {
    assertNotEquals(firstCard, null);

    secondCard =
        new GoldCard(
            2,
            Resources.FUNGI,
            PointsType.FIVE,
            mockRequiredResources,
            mockFrontCorners,
            mockBackCorners);
    assertNotEquals(firstCard, secondCard);

    secondCard =
        new GoldCard(
            1,
            Resources.ANIMAL,
            PointsType.ONE,
            mockRequiredResources,
            mockFrontCorners,
            mockBackCorners);
    assertEquals(firstCard, secondCard);

    secondCard =
        new ResourceCard(1, Resources.ANIMAL, PointsType.ONE, mockFrontCorners, mockBackCorners);
    assertNotEquals(firstCard, secondCard);
  }
}
