package it.polimi.ingsw.model.card.objective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.Card;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.deck.Deck;
import it.polimi.ingsw.model.deck.GoldDeckCreator;
import it.polimi.ingsw.model.deck.ObjectiveDeckCreator;
import it.polimi.ingsw.model.deck.ResourceDeckCreator;
import it.polimi.ingsw.model.deck.StarterDeckCreator;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PatternObjectiveStrategyTest {
  List<StarterCard> starterDeck;
  List<GoldCard> goldsDeck;
  List<ResourceCard> resourceDeck;
  List<ObjectiveCard> objectiveDeck;

  List<Observer> obsList = new ArrayList<>();
  AtomicInteger atomicInt = new AtomicInteger(0);

  @BeforeEach
  void init() {
    resourceDeck = deckToList(ResourceDeckCreator.createDeck(obsList, atomicInt));
    goldsDeck = deckToList(GoldDeckCreator.createDeck(obsList, atomicInt));
    starterDeck = deckToList(StarterDeckCreator.createDeck(obsList, atomicInt));
    objectiveDeck = deckToList(ObjectiveDeckCreator.createDeck(obsList, atomicInt));
  }

  @Test
  void testGetCompletedOccurrencesDiagonal() {
    ObjectiveCard objective = findCard(objectiveDeck, 90);

    PlayerBoard board = new PlayerBoard(findCard(starterDeck, 81), CardSide.FRONT, obsList, atomicInt);
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 31), CardSide.FRONT, new Coords(1, 1));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 32), CardSide.FRONT, new Coords(2, 0));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(0, 2));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(3, -1));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 35), CardSide.FRONT, new Coords(4, -2));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(-1, 3));
    assertEquals(2 * objective.getPoints(), objective.computePoints(board));
  }

  @Test
  void testGetCompletedOccurrencesBrokenDiagonal() {
    ObjectiveCard objective = findCard(objectiveDeck, 90);

    PlayerBoard board = new PlayerBoard(findCard(starterDeck, 81), CardSide.FRONT, obsList, atomicInt);
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 31), CardSide.FRONT, new Coords(1, 1));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 32), CardSide.FRONT, new Coords(2, 0));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(0, 2));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 1), CardSide.FRONT, new Coords(3, -1));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 35), CardSide.FRONT, new Coords(4, -2));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(-1, 3));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));
  }

  @Test
  void testGetCompletedOccurrencesL() {
    ObjectiveCard objective = findCard(objectiveDeck, 91);

    PlayerBoard board = new PlayerBoard(findCard(starterDeck, 81), CardSide.FRONT, obsList, atomicInt);
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 1), CardSide.FRONT, new Coords(-1, 1));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 2), CardSide.FRONT, new Coords(-1, -1));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 11), CardSide.FRONT, new Coords(0, -2));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 3), CardSide.FRONT, new Coords(-1, -3));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 11), CardSide.FRONT, new Coords(0, -4));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));
  }

  @Test
  void testGetCompletedOccurrencesLRev() {
    ObjectiveCard objective = findCard(objectiveDeck, 94);

    PlayerBoard board = new PlayerBoard(findCard(starterDeck, 81), CardSide.FRONT, obsList, atomicInt);
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 31), CardSide.FRONT, new Coords(0, -2));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 21), CardSide.FRONT, new Coords(-1, -1));
    assertEquals(0, objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 32), CardSide.FRONT, new Coords(0, -4));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(2, 0));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 23), CardSide.FRONT, new Coords(1, 1));
    assertEquals(1 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(2, -2));
    assertEquals(2 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 23), CardSide.FRONT, new Coords(-1, -3));
    assertEquals(2 * objective.getPoints(), objective.computePoints(board));

    playCard(board, findCard(resourceDeck, 35), CardSide.FRONT, new Coords(0, -6));
    assertEquals(2 * objective.getPoints(), objective.computePoints(board));
  }

  <T extends Card> List<T> deckToList(Deck<T> deck) {
    List<T> list = new ArrayList<>();

    Optional<T> card = deck.draw(null, 0);
    while (card.isPresent()) {
      list.add(card.get());
      card = deck.draw(null, 0);
    }

    return list;
  }

  <T extends Card> T findCard(List<T> list, int id) {
    for (T card : list)
      if (card.id == id)
        return card;
    return null;
  }

  void playCard(PlayerBoard board, PlayableCard card, CardSide side, Coords coords) {
    board.placeCard(PlayerToken.BLUE, coords, card, side);
  }
}
