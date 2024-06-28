package it.polimi.ingsw.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerBoardTest {
  PlayerToken playerToken;
  PlayerBoard playerBoard;
  StarterCard starterCard;
  ResourceCard firstResourceCard, secondResourceCard;
  GoldCard firstGoldCard, secondGoldCard;
  CardSide starterCardSide;

  List<it.polimi.ingsw.controller.observer.Observer> observers;
  AtomicInteger lastEventId;

  @BeforeEach
  void setUp() {
    playerToken = PlayerToken.RED;
    observers = new ArrayList<>();
    lastEventId = new AtomicInteger(0);

    starterCard = new StarterCard(0,
        new HashSet<>(Arrays.asList(Resources.INSECT)),
        new HashMap<>() {{
          put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
          put(CornerPosition.TOP_RIGHT, new Corner(Resources.PLANT, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_LEFT, new Corner(Resources.INSECT, CornerTypes.VISIBLE));
        }},
        new HashMap<>() {{
          put(CornerPosition.TOP_LEFT, new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
          put(CornerPosition.TOP_RIGHT, new Corner(Resources.PLANT, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_RIGHT, new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_LEFT, new Corner(Resources.INSECT, CornerTypes.VISIBLE));
        }}
        );

    firstResourceCard = new ResourceCard(
      1,
      Resources.FUNGI,
      PointsType.ZERO,
      new HashMap<>() {{
        put(CornerPosition.TOP_LEFT, new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.HIDDEN));
      }},
      new HashMap<>() {{
        put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
      }}
      );

    secondResourceCard = new ResourceCard(
      1,
      Resources.ANIMAL,
      PointsType.ZERO,
      new HashMap<>() {{
        put(CornerPosition.TOP_LEFT, new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.HIDDEN));
        put(CornerPosition.BOTTOM_RIGHT, new Corner(Items.MANUSCRIPT, CornerTypes.HIDDEN));
        put(CornerPosition.BOTTOM_LEFT, new Corner(Resources.INSECT, CornerTypes.VISIBLE));
      }},
      new HashMap<>() {{
        put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
      }}
      );

    firstGoldCard = new GoldCard(
      2,
      Resources.FUNGI,
      PointsType.ONE_PER_QUILL,
      new HashMap<>() {{
        put(Resources.FUNGI, 2);
        put(Resources.PLANT, 1);
      }},
      new HashMap<>() {{
        put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.HIDDEN));
        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_RIGHT, new Corner(Items.QUILL, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
      }},
      new HashMap<>() {{
        put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
      }}
      );
    
      secondGoldCard = new GoldCard(
        2,
        Resources.FUNGI,
        PointsType.ONE_PER_QUILL,
        new HashMap<>() {{
          put(Resources.FUNGI, 3);
          put(Resources.ANIMAL, 2);
          put(Resources.INSECT, 1);
        }},
        new HashMap<>() {{
          put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.HIDDEN));
          put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_RIGHT, new Corner(Items.QUILL, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
        }},
        new HashMap<>() {{
          put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
          put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.HIDDEN));
          put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
          put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
        }}
        );

    starterCardSide = CardSide.FRONT;
  
    playerBoard = new PlayerBoard(starterCard, starterCardSide, observers, lastEventId);
  }

  @Test
  void constructorTest() {
    assertEquals(2, playerBoard.playerElements.get(Resources.INSECT));
    assertEquals(1, playerBoard.playerElements.get(Resources.PLANT));
    assertEquals(0, playerBoard.playerElements.get(Resources.ANIMAL));
    assertEquals(0, playerBoard.playerElements.get(Resources.FUNGI));
    assertEquals(0, playerBoard.playerElements.get(Items.INKWELL));
    assertEquals(0, playerBoard.playerElements.get(Items.QUILL));
    assertEquals(0, playerBoard.playerElements.get(Items.MANUSCRIPT));

    assertEquals(starterCard, playerBoard.getCard(new Coords(0, 0)));
    assertEquals(1, playerBoard.board.keySet().size());

    assertEquals(1, playerBoard.cardsPlacementOrder.size());
    assertTrue(playerBoard.cardsPlacementOrder.containsKey(0));
    assertEquals(starterCard, playerBoard.cardsPlacementOrder.get(0).first);
    assertEquals(starterCardSide, playerBoard.cardsPlacementOrder.get(0).first.getPlayedSide());
    assertEquals(new Coords(0,0), playerBoard.cardsPlacementOrder.get(0).second);
  }

  @Test
  void canPlaceCardAtTest() {
    assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, 1), firstResourceCard, CardSide.FRONT));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(1, 1), firstResourceCard, CardSide.FRONT));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(1, -1), firstResourceCard, CardSide.FRONT));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(-1, -1), firstResourceCard, CardSide.FRONT));

    assertFalse(playerBoard.canPlaceCardAt(new Coords(0, 0), firstResourceCard, CardSide.FRONT));
    assertFalse(playerBoard.canPlaceCardAt(new Coords(-1, 0), firstResourceCard, CardSide.FRONT));
    playerBoard.placeCard(PlayerToken.RED, new Coords(1, 1), firstResourceCard, CardSide.FRONT);

    assertTrue(playerBoard.canPlaceCardAt(new Coords(0, 2), secondResourceCard, CardSide.BACK));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(2, 2), secondResourceCard, CardSide.BACK));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(2, 0), secondResourceCard, CardSide.BACK));
    assertFalse(playerBoard.canPlaceCardAt(new Coords(0, 0), secondResourceCard, CardSide.BACK));

    assertTrue(playerBoard.canPlaceCardAt(new Coords(1, -1), secondResourceCard, CardSide.BACK));
    playerBoard.placeCard(PlayerToken.RED, new Coords(1, -1), secondResourceCard, CardSide.FRONT);

    assertFalse(playerBoard.canPlaceCardAt(new Coords(2, 0), firstGoldCard, CardSide.BACK));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(0, 2), firstGoldCard, CardSide.BACK));
    assertTrue(playerBoard.canPlaceCardAt(new Coords(0, 2), secondGoldCard, CardSide.BACK));
    playerBoard.placeCard(PlayerToken.RED, new Coords(-1, 1), secondGoldCard, CardSide.BACK);
    assertFalse(playerBoard.canPlaceCardAt(new Coords( 0, 2), firstGoldCard, CardSide.BACK));
  }

  @Test
  void placeCardTest() {
    assertEquals(CornerTypes.VISIBLE, playerBoard.getCard(new Coords(0,0)).getActiveCorners().get(CornerPosition.TOP_RIGHT).type);
    
    playerBoard.placeCard(playerToken, new Coords(1,1), firstResourceCard, CardSide.FRONT);
    assertEquals(firstResourceCard, playerBoard.getCard(new Coords(1,1)));
    assertEquals(CornerTypes.COVERED, playerBoard.getCard(new Coords(0,0)).getActiveCorners().get(CornerPosition.TOP_RIGHT).type);
    assertTrue(playerBoard.cardsPlacementOrder.containsKey(1));
    assertEquals(2, playerBoard.cardsPlacementOrder.size());
    assertEquals(firstResourceCard, playerBoard.cardsPlacementOrder.get(1).first);
    assertEquals(CardSide.FRONT, playerBoard.cardsPlacementOrder.get(1).first.getPlayedSide());
    assertEquals(new Coords(1,1), playerBoard.cardsPlacementOrder.get(1).second);
  }

  @Test
  void adjacentCoordsTest() {
    Map<CornerPosition, Coords> result = playerBoard.adjacentCoords(new Coords(0,0));

    assertEquals(new Coords(-1, 1), result.get(CornerPosition.TOP_LEFT));
    assertEquals(new Coords(1, 1), result.get(CornerPosition.TOP_RIGHT));
    assertEquals(new Coords(1, -1), result.get(CornerPosition.BOTTOM_RIGHT));
    assertEquals(new Coords(-1, -1), result.get(CornerPosition.BOTTOM_LEFT));
  }

  @Test 
  void adjacentCornersTest() {
    assertEquals(0, playerBoard.adjacentCorners(new Coords(0, 0)).keySet().size());

    playerBoard.placeCard(playerToken, new Coords(-1, 1), firstResourceCard, CardSide.BACK);  
    playerBoard.placeCard(playerToken, new Coords(-2, 2), secondResourceCard, CardSide.BACK);
    playerBoard.placeCard(playerToken, new Coords(-3, 1), firstGoldCard, CardSide.BACK);
    playerBoard.placeCard(playerToken, new Coords(-1, -1), secondGoldCard, CardSide.BACK);

    assertEquals(3, playerBoard.adjacentCorners(new Coords(-2,0)).size());
    assertEquals(firstGoldCard.getActiveCorners().get(CornerPosition.BOTTOM_RIGHT), playerBoard.adjacentCorners(new Coords(-2,0)).get(CornerPosition.TOP_LEFT));
    assertEquals(firstResourceCard.getActiveCorners().get(CornerPosition.BOTTOM_LEFT), playerBoard.adjacentCorners(new Coords(-2,0)).get(CornerPosition.TOP_RIGHT));
    assertEquals(secondGoldCard.getActiveCorners().get(CornerPosition.TOP_LEFT), playerBoard.adjacentCorners(new Coords(-2,0)).get(CornerPosition.BOTTOM_RIGHT));
  }

  @Test
  void adjacentCardsTest() {
    assertEquals(0, playerBoard.adjacentCards(new Coords(0, 0)).keySet().size());

    playerBoard.placeCard(playerToken, new Coords(-1, 1), firstResourceCard, CardSide.BACK);  
    playerBoard.placeCard(playerToken, new Coords(-2, 2), secondResourceCard, CardSide.BACK);
    playerBoard.placeCard(playerToken, new Coords(-3, 1), firstGoldCard, CardSide.BACK);
    playerBoard.placeCard(playerToken, new Coords(-1, -1), secondGoldCard, CardSide.BACK);

    assertEquals(3, playerBoard.adjacentCards(new Coords(-2,0)).size());
    assertEquals(firstGoldCard, playerBoard.adjacentCards(new Coords(-2,0)).get(CornerPosition.TOP_LEFT));
    assertEquals(firstResourceCard, playerBoard.adjacentCards(new Coords(-2,0)).get(CornerPosition.TOP_RIGHT));
    assertEquals(secondGoldCard, playerBoard.adjacentCards(new Coords(-2,0)).get(CornerPosition.BOTTOM_RIGHT));
  }

  @Test
  void availableCoordsTest() {
    assertEquals(4, playerBoard.availableCoords().size());
    assertTrue(playerBoard.availableCoords().contains(new Coords(-1, 1)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(1, 1)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(1, -1)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(-1, -1)));

    playerBoard.placeCard(playerToken, new Coords(1,1), firstGoldCard, CardSide.FRONT);
    assertEquals(5, playerBoard.availableCoords().size());
    assertTrue(playerBoard.availableCoords().contains(new Coords(-1, 1)));
    assertFalse(playerBoard.availableCoords().contains(new Coords(1, 1)));
    assertFalse(playerBoard.availableCoords().contains(new Coords(0, 2)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(2, 2)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(2, 0)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(1, -1)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(-1, -1)));

    playerBoard.placeCard(playerToken, new Coords(2,0), firstResourceCard, CardSide.FRONT);
    assertEquals(5, playerBoard.availableCoords().size());
    assertFalse(playerBoard.availableCoords().contains(new Coords(2, 0)));
    assertFalse(playerBoard.availableCoords().contains(new Coords(1, -1)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(3, 1)));
    assertTrue(playerBoard.availableCoords().contains(new Coords(3, -1)));
  }

  @Test
  void updatePlayerElementsTest() {
    playerBoard.placeCard(playerToken, new Coords(1,1), firstResourceCard, CardSide.FRONT);
    assertEquals(2, playerBoard.playerElements.get(Resources.INSECT));
    assertEquals(1, playerBoard.playerElements.get(Resources.PLANT));

    playerBoard.updatePlayerElements(playerToken, new Coords(1,1));
    assertEquals(2, playerBoard.playerElements.get(Resources.INSECT));
    assertEquals(0, playerBoard.playerElements.get(Resources.PLANT));
    assertEquals(1, playerBoard.playerElements.get(Resources.FUNGI));

    playerBoard.placeCard(playerToken, new Coords(2,0), secondGoldCard, CardSide.BACK);
    playerBoard.updatePlayerElements(playerToken, new Coords(2,0));
    assertEquals(2, playerBoard.playerElements.get(Resources.INSECT));
    assertEquals(0, playerBoard.playerElements.get(Resources.PLANT));
    assertEquals(2, playerBoard.playerElements.get(Resources.FUNGI));
  }
}
