package it.polimi.ingsw.model.card.objective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.Card;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.deck.Deck;
import it.polimi.ingsw.model.deck.ObjectiveDeckCreator;
import it.polimi.ingsw.model.deck.StarterDeckCreator;
import it.polimi.ingsw.model.player.PlayerBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemsObjectiveStrategyTest {
  List<ObjectiveCard> objectiveDeck;
  List<StarterCard> starterDeck;

  List<Observer> obsList = new ArrayList<>();
  AtomicInteger atomicInt = new AtomicInteger(0);
  
  @BeforeEach
  void init() {
    objectiveDeck = deckToList(ObjectiveDeckCreator.createDeck(obsList,atomicInt));
    starterDeck = deckToList(StarterDeckCreator.createDeck(obsList,atomicInt));
  }

  @Test
  void testGetCompletedOccurrences1() {
    ObjectiveCard objective = findCard(objectiveDeck, 98);

    PlayerBoard board = new PlayerBoard(findCard(starterDeck, 81), CardSide.FRONT, obsList,atomicInt);
    Map<Elements, Integer> map = board.playerElements;

    assertEquals(0, objective.computePoints(board));

    map.put(Resources.INSECT, 3);

    assertEquals(1 * objective.getPoints(), objective.computePoints(board));
  }

  @Test
  void testGetCompletedOccurrences2() {
    ObjectiveCard objective = findCard(objectiveDeck, 102);

    PlayerBoard board = new PlayerBoard(findCard(starterDeck, 81), CardSide.FRONT, obsList, atomicInt);
    Map<Elements, Integer> playerItems = board.playerElements;

    assertEquals(0, objective.computePoints(board));

    playerItems.put(Items.QUILL, 2);

    assertEquals(1 * objective.getPoints(), objective.computePoints(board));
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
    for (T card : list) if (card.id == id) return card;
    return null;
  }
}
