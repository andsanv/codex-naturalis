/*package it.polimi.ingsw.controller;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.player.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameModelUpdaterTest {
  private GameModel model;
  private GameModelUpdater gameModelUpdater;
  private Optional<StarterCard> starterCard;
  private Optional<ObjectiveCard> objectiveCard;

  private PlayableCard resourceCard1, resourceCard2;
  private StarterCard specificStarterCard;
  private Map<CornerPosition, Corner> frontCorners, backCorners;

  @BeforeEach
  void init() {
    model = new GameModel();
    gameModelUpdater = new GameModelUpdater(model);

    starterCard = gameModelUpdater.drawStarterCard();
    objectiveCard = gameModelUpdater.drawObjectiveCard();

    frontCorners = new HashMap<>();
    backCorners = new HashMap<>();

    frontCorners.put(CornerPosition.TOP_LEFT, new Corner(Resources.INSECT, CornerTypes.VISIBLE));
    frontCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.HIDDEN));
    frontCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(Resources.PLANT, CornerTypes.VISIBLE));
    frontCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.HIDDEN));

    backCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
    backCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
    backCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
    backCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

    Set<Resources> centralElements = new HashSet<>();
    centralElements.insert(Resources.FUNGI);
    centralElements.insert(Resources.INSECT);

    resourceCard1 =
        new ResourceCard(0, Resources.ANIMAL, PointsType.ONE, frontCorners, backCorners);
    resourceCard2 =
        new ResourceCard(1, Resources.ANIMAL, PointsType.ONE, frontCorners, backCorners);
    specificStarterCard = new StarterCard(2, centralElements, backCorners, backCorners);

    gameModelUpdater.addPlayer(
        PlayerToken.RED, starterCard.draw(), CardSide.FRONT, objectiveCard.draw());
    gameModelUpdater.addPlayer(
        PlayerToken.GREEN, starterCard.draw(), CardSide.FRONT, objectiveCard.draw());
    gameModelUpdater.addPlayer(
        PlayerToken.YELLOW, starterCard.draw(), CardSide.FRONT, objectiveCard.draw());

    gameModelUpdater.setScoreTrack(
        new ArrayList<>(Arrays.asList(PlayerToken.RED, PlayerToken.GREEN, PlayerToken.YELLOW)));
  }

  @Test
  void playCardPlacementTest() {
    Player player = model.tokenToPlayer.draw(PlayerToken.RED);
    PlayerHand playerHand = player.getHand();
    PlayerBoard playerBoard = player.getBoard();

    playerHand.addCard(resourceCard1);
    int tempSize = playerHand.getCards().size();

    // card placement should be successful
    assertTrue(
        gameModelUpdater.playCard(
            PlayerToken.RED, new Coords(1, 1), resourceCard1, CardSide.FRONT));

    // should not be possible to place a card at occupied coords
    assertFalse(
        gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), resourceCard2, CardSide.BACK));

    // hand size should change in the following way
    assertEquals(0, playerHand.getCards().size());
    assertEquals(tempSize - 1, playerHand.getCards().size());

    // card placed should be the card that was actually played, and the corner being covered should
    // switch to a COVERED corner.
    assertEquals(resourceCard1, playerBoard.getCard(new Coords(1, 1)));
    assertEquals(
        CornerTypes.COVERED,
        playerBoard
            .getCard(new Coords(0, 0))
            .getActiveCorners()
            .draw(CornerPosition.TOP_RIGHT)
            .getType());

    // cornerType of covering card should not change
    assertEquals(
        CornerTypes.HIDDEN,
        playerBoard
            .getCard(new Coords(1, 1))
            .getActiveCorners()
            .draw(CornerPosition.BOTTOM_LEFT)
            .getType());

    playerHand.addCard(resourceCard2);
    // corner of card below is hidden, so playCard should fail
    assertFalse(
        gameModelUpdater.playCard(PlayerToken.RED, new Coords(2, 2), resourceCard2, CardSide.BACK));
    // playCard should fail in invalid coords
    assertFalse(
        gameModelUpdater.playCard(PlayerToken.RED, new Coords(0, 1), resourceCard2, CardSide.BACK));

    // all adjacent corners should be covered when placing a card
    gameModelUpdater.playCard(PlayerToken.RED, new Coords(0, 2), resourceCard2, CardSide.BACK);
    assertTrue(
        gameModelUpdater.playCard(
            PlayerToken.RED, new Coords(-1, 1), resourceCard2, CardSide.BACK));
    assertEquals(
        CornerTypes.VISIBLE,
        playerBoard
            .getCard(new Coords(-1, 1))
            .getActiveCorners()
            .draw(CornerPosition.BOTTOM_RIGHT)
            .getType());
    assertEquals(
        CornerTypes.COVERED,
        playerBoard
            .getCard(new Coords(0, 0))
            .getActiveCorners()
            .draw(CornerPosition.TOP_LEFT)
            .getType());
    assertEquals(
        CornerTypes.COVERED,
        playerBoard
            .getCard(new Coords(0, 2))
            .getActiveCorners()
            .draw(CornerPosition.BOTTOM_LEFT)
            .getType());
  }

  @Test
  void playCardPointsAndResourcesTest() {
    // let's only consider one player
    gameModelUpdater.addPlayer(
        PlayerToken.BLUE, specificStarterCard, CardSide.BACK, objectiveCard.draw());
    gameModelUpdater.setScoreTrack(
        new ArrayList<>(
            Arrays.asList(
                PlayerToken.RED, PlayerToken.GREEN, PlayerToken.YELLOW, PlayerToken.BLUE)));

    Player player = model.tokenToPlayer.draw(PlayerToken.BLUE);
    PlayerHand playerHand = player.getHand();
    PlayerBoard playerBoard = player.getBoard();

    // playCard should be successful and resources should be updated summing all resources
    gameModelUpdater.playCard(PlayerToken.BLUE, new Coords(1, 1), resourceCard1, CardSide.FRONT);
    assertEquals(2, playerBoard.getPlayerItems().draw(Resources.INSECT));
    assertEquals(1, playerBoard.getPlayerItems().draw(Resources.FUNGI));
    assertEquals(0, playerBoard.getPlayerItems().draw(Resources.ANIMAL));
    assertEquals(1, playerBoard.getPlayerItems().draw(Resources.PLANT));

    // one INSECT resource should be covered, and a INSECT resource and a PLANT resource should be
    // added to resources count
    gameModelUpdater.playCard(PlayerToken.BLUE, new Coords(0, 2), resourceCard2, CardSide.FRONT);
    assertEquals(2, playerBoard.getPlayerItems().draw(Resources.INSECT));
    assertEquals(1, playerBoard.getPlayerItems().draw(Resources.FUNGI));
    assertEquals(0, playerBoard.getPlayerItems().draw(Resources.ANIMAL));
    assertEquals(2, playerBoard.getPlayerItems().draw(Resources.PLANT));

    // every card we placed should give 1 point each
    assertEquals(2, model.getScoreTrack().getScores().draw(PlayerToken.BLUE));

    // changing card's pointType
    resourceCard2 =
        new ResourceCard(
            1, Resources.ANIMAL, PointsType.TWO_PER_COVERED_CORNER, frontCorners, backCorners);

    // card covers 1 corner, so 2 more points should be given. A PLANT resource is covered, a PLANT
    // resource and an ANIMAL resource are added
    gameModelUpdater.playCard(PlayerToken.BLUE, new Coords(2, 0), resourceCard2, CardSide.BACK);
    assertEquals(2, playerBoard.getPlayerItems().draw(Resources.INSECT));
    assertEquals(1, playerBoard.getPlayerItems().draw(Resources.FUNGI));
    assertEquals(1, playerBoard.getPlayerItems().draw(Resources.ANIMAL));
    assertEquals(1, playerBoard.getPlayerItems().draw(Resources.PLANT));

    assertEquals(4, model.getScoreTrack().getScores().draw(PlayerToken.BLUE));

    // card placed covers 2 corners, so 4 points should be given.
    gameModelUpdater.playCard(PlayerToken.BLUE, new Coords(1, -1), resourceCard2, CardSide.BACK);
    assertEquals(8, model.getScoreTrack().getScores().draw(PlayerToken.BLUE));
  }

  @Test
  void drawResourceDeckCardTest() {
    // hand should be empty
    assertTrue(model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().isEmpty());

    // hand should contain the drawn cards until size 3 of playerHand is reached
    gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
    assertEquals(1, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
    assertEquals(2, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
    assertEquals(3, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    int tempSize = model.getResourceCardsDeck().size();
    assertFalse(
        gameModelUpdater.drawResourceDeckCard(
            PlayerToken.RED)); // hand is full, so no card should be drawn
    assertEquals(
        3,
        model
            .tokenToPlayer
            .draw(PlayerToken.RED)
            .getHand()
            .getCards()
            .size()); // hand size should not change
    assertEquals(
        tempSize,
        model
            .getResourceCardsDeck()
            .size()); // deck size should not change, as hand is full and no card should be drawn

    // emptying deck
    for (int i = 0; !model.getResourceCardsDeck().isEmpty(); i++) {
      gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);

      model
          .tokenToPlayer
          .draw(PlayerToken.GREEN)
          .getHand()
          .getCards()
          .forEach(card -> model.tokenToPlayer.draw(PlayerToken.GREEN).getHand().remove(card));
    }

    ArrayList<PlayableCard> tempCards =
        new ArrayList<>(model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards());

    // draw should not be completed as deck is empty
    assertFalse(gameModelUpdater.drawResourceDeckCard(PlayerToken.RED));
    // hand should not be changed since deck is empty
    assertTrue(
        model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().containsAll(tempCards)
            && tempCards.containsAll(
                model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards()));
  }

  @Test
  void drawGoldDeckCardTest() {
    // following tests are exactly the same as the previous test method. Implemented for
    // completeness
    assertTrue(model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().isEmpty());

    gameModelUpdater.drawGoldDeckCard(PlayerToken.RED);
    assertEquals(1, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    gameModelUpdater.drawGoldDeckCard(PlayerToken.RED);
    assertEquals(2, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    gameModelUpdater.drawGoldDeckCard(PlayerToken.RED);
    assertEquals(3, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    int tempSize = model.getGoldCardsDeck().size();
    assertFalse(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));
    assertEquals(3, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());
    assertEquals(tempSize, model.getGoldCardsDeck().size());

    for (int i = 0; !model.getGoldCardsDeck().isEmpty(); i++) {
      gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);

      model
          .tokenToPlayer
          .draw(PlayerToken.GREEN)
          .getHand()
          .getCards()
          .forEach(card -> model.tokenToPlayer.draw(PlayerToken.GREEN).getHand().remove(card));
    }

    ArrayList<PlayableCard> tempCards =
        new ArrayList<>(model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards());

    assertFalse(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));
    assertTrue(
        model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().containsAll(tempCards)
            && tempCards.containsAll(
                model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards()));
  }

  @Test
  void drawVisibleResourceCardTest() {
    // hand should be empty
    assertTrue(model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().isEmpty());

    // there should be 2 visible cards
    assertEquals(2, model.getVisibleResourceCards().size());

    // drawCard should be successful and card should be added to hand
    assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 0));
    assertEquals(1, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());
    assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 1));
    assertEquals(2, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    // cards drawn into player's hand must not be in the visibleCards list
    model
        .tokenToPlayer
        .draw(PlayerToken.RED)
        .getHand()
        .getCards()
        .forEach(
            card -> assertFalse(model.getVisibleResourceCards().contains((ResourceCard) card)));

    // amount of visible cards should still be 2 as the deck is not empty
    assertEquals(2, model.getVisibleResourceCards().size());

    // emptying deck for further tests
    for (int i = 0; !model.getResourceCardsDeck().isEmpty(); i++) {
      gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);

      model
          .tokenToPlayer
          .draw(PlayerToken.GREEN)
          .getHand()
          .getCards()
          .forEach(card -> model.tokenToPlayer.draw(PlayerToken.GREEN).getHand().remove(card));
    }

    // first draw should complete successfully
    assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 1));
    // second should fail as there were no more cards in the deck to fill the visible ones
    assertFalse(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 1));

    // same thing, but on the other position
    assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.YELLOW, 0));
    assertFalse(gameModelUpdater.drawVisibleResourceCard(PlayerToken.YELLOW, 0));
  }

  @Test
  void drawVisibleGoldCardTest() {
    // following tests are exactly the same as the previous test method. Implemented for
    // completeness
    assertTrue(model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().isEmpty());

    assertEquals(2, model.getVisibleGoldCards().size());

    assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 0));
    assertEquals(1, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());
    assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));
    assertEquals(2, model.tokenToPlayer.draw(PlayerToken.RED).getHand().getCards().size());

    model
        .tokenToPlayer
        .draw(PlayerToken.RED)
        .getHand()
        .getCards()
        .forEach(card -> assertFalse(model.getVisibleGoldCards().contains((GoldCard) card)));

    assertEquals(2, model.getVisibleGoldCards().size());

    for (int i = 0; !model.getGoldCardsDeck().isEmpty(); i++) {
      gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
      gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);

      model
          .tokenToPlayer
          .draw(PlayerToken.GREEN)
          .getHand()
          .getCards()
          .forEach(card -> model.tokenToPlayer.draw(PlayerToken.GREEN).getHand().remove(card));
    }

    assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));
    assertFalse(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));

    assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.YELLOW, 0));
    assertFalse(gameModelUpdater.drawVisibleGoldCard(PlayerToken.YELLOW, 0));
  }

  @Test
  void addPlayerTest() {
    assertFalse(
        gameModelUpdater.addPlayer(
            PlayerToken.RED, starterCard.draw(), CardSide.FRONT, objectiveCard.draw()));

    assertTrue(
        gameModelUpdater.addPlayer(
            PlayerToken.BLUE, starterCard.draw(), CardSide.FRONT, objectiveCard.draw()));
    assertFalse(
        gameModelUpdater.addPlayer(
            PlayerToken.BLUE, starterCard.draw(), CardSide.FRONT, objectiveCard.draw()));
  }
}
*/