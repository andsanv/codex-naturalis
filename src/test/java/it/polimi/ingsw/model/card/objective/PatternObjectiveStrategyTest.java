package it.polimi.ingsw.model.card.objective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class PatternObjectiveStrategyTest {
    List<StarterCard> starterDeck;
    List<GoldCard> goldsDeck;
    List<ResourceCard> resourceDeck;
    List<ObjectiveCard> objectiveDeck;

    @BeforeEach
    void init() {
        resourceDeck = deckToList(ResourceDeckCreator.createDeck());
        goldsDeck = deckToList(GoldDeckCreator.createDeck());
        starterDeck = deckToList(StarterDeckCreator.createDeck());
        objectiveDeck = deckToList(ObjectiveDeckCreator.createDeck());
    }

    @Test
    void testGetCompletedOccurrencesDiagonal() {
        ObjectiveCard objective = findCard(objectiveDeck, 3);

        PlayerBoard board = new PlayerBoard(findCard(starterDeck, 0), CardSide.FRONT);
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 30), CardSide.FRONT, new Coords(1, 1));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 31), CardSide.FRONT, new Coords(2, 0));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 32), CardSide.FRONT, new Coords(0, 2));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(3, -1));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(4, -2));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(-1, 3));
        assertEquals(2*objective.getPoints(), objective.computePoints(board));
    }

    @Test
    void testGetCompletedOccurrencesBrokenDiagonal() {
        ObjectiveCard objective = findCard(objectiveDeck, 3);

        PlayerBoard board = new PlayerBoard(findCard(starterDeck, 0), CardSide.FRONT);
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 30), CardSide.FRONT, new Coords(1, 1));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 31), CardSide.FRONT, new Coords(2, 0));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 32), CardSide.FRONT, new Coords(0, 2));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 0), CardSide.FRONT, new Coords(3, -1));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(4, -2));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(-1, 3));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));
    }

    @Test
    void testGetCompletedOccurrencesL() {
        ObjectiveCard objective = findCard(objectiveDeck, 4);

        PlayerBoard board = new PlayerBoard(findCard(starterDeck, 0), CardSide.FRONT);
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 0), CardSide.FRONT, new Coords(-1, 1));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 1), CardSide.FRONT, new Coords(-1, -1));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 10), CardSide.FRONT, new Coords(0, -2));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 2), CardSide.FRONT, new Coords(-1, -3));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 10), CardSide.FRONT, new Coords(0, -4));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));
    }

    @Test
    void testGetCompletedOccurrencesLRev() {
        ObjectiveCard objective = findCard(objectiveDeck, 7);

        PlayerBoard board = new PlayerBoard(findCard(starterDeck, 0), CardSide.FRONT);
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 30), CardSide.FRONT, new Coords(0, -2));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 20), CardSide.FRONT, new Coords(-1, -1));
        assertEquals(0, objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 31), CardSide.FRONT, new Coords(0, -4));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 32), CardSide.FRONT, new Coords(2, 0));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 22), CardSide.FRONT, new Coords(1, 1));
        assertEquals(1*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 33), CardSide.FRONT, new Coords(2, -2));
        assertEquals(2*objective.getPoints(), objective.computePoints(board));
    
        playCard(board, findCard(resourceDeck, 22), CardSide.FRONT, new Coords(-1, -3));
        assertEquals(2*objective.getPoints(), objective.computePoints(board));

        playCard(board, findCard(resourceDeck, 34), CardSide.FRONT, new Coords(0, -6));
        assertEquals(2*objective.getPoints(), objective.computePoints(board));
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

    void playCard(PlayerBoard board, PlayableCard card, CardSide side, Coords coords) {
        card.playSide(side);
        board.setCard(coords, card);
    }
}
