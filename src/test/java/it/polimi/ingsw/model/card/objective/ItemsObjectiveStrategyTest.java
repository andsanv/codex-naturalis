package it.polimi.ingsw.model.card.objective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class ItemsObjectiveStrategyTest {
    List<ObjectiveCard> objectiveDeck;
    List<StarterCard> starterDeck;


    @BeforeEach
    void init() {
        objectiveDeck = deckToList(ObjectiveDeckCreator.createDeck());
        starterDeck = deckToList(StarterDeckCreator.createDeck());
    }

    @Test
    void testGetCompletedOccurrences1() {
        ObjectiveCard objective = findCard(objectiveDeck, 11);

        PlayerBoard board = new PlayerBoard(findCard(starterDeck, 0), CardSide.FRONT);
        Map<Elements, Integer> map = board.getPlayerItems();

        assertEquals(0, objective.computePoints(board));

        map.put(Resources.INSECT, 3);

        assertEquals(1*objective.getPoints(), objective.computePoints(board));
    }

    @Test
    void testGetCompletedOccurrences2() {
        ObjectiveCard objective = findCard(objectiveDeck, 15);

        PlayerBoard board = new PlayerBoard(findCard(starterDeck, 0), CardSide.FRONT);
        Map<Elements, Integer> playerItems = board.getPlayerItems();

        assertEquals(0, objective.computePoints(board));

        playerItems.put(Items.QUILL, 2);

        assertEquals(1*objective.getPoints(), objective.computePoints(board));
    }

    <T extends Card> List<T> deckToList(Deck<T> deck) {
        List<T> list = new ArrayList<>();

        Optional<T> card = deck.draw();
        while (card.isPresent()) {
            list.add(card.get());
            card = deck.draw();
        }

        return list;
    }

    <T extends Card> T findCard(List<T> list, int id) {
        for (T card : list)
            if (card.getId() == id)
                return card;
        return null;
    }
}