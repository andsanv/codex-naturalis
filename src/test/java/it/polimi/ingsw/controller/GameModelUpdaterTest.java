package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.deck.Deck;
import it.polimi.ingsw.model.player.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameModelUpdaterTest {
    private GameModel model;
    private GameModelUpdater gameModelUpdater;
    private Optional<StarterCard> starterCard;
    private Optional<ObjectiveCard> objectiveCard;

    @BeforeEach
    void init() {
        model = new GameModel();
        gameModelUpdater = new GameModelUpdater(model);

        starterCard = gameModelUpdater.drawStarterCard();
        objectiveCard = gameModelUpdater.drawObjectiveCard();

        gameModelUpdater.addPlayer(PlayerToken.RED, starterCard.get(), CardSide.FRONT, objectiveCard.get());
        gameModelUpdater.addPlayer(PlayerToken.GREEN, starterCard.get(), CardSide.FRONT, objectiveCard.get());
        gameModelUpdater.addPlayer(PlayerToken.YELLOW, starterCard.get(), CardSide.FRONT, objectiveCard.get());

        gameModelUpdater.setScoreTrack(new ArrayList<>(Arrays.asList(PlayerToken.RED, PlayerToken.GREEN, PlayerToken.YELLOW)));
    }

    @Test
    void playCard() {
        Player player = model.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand playerHand = player.getHand();
        PlayerBoard playerBoard = player.getBoard();

        gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
        PlayableCard card = playerHand.getCards().get(0);
        int tempSize = playerHand.getCards().size();
        assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), card, CardSide.FRONT));

        // should not be possible to place a card at occupied coords
        assertFalse(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), card, CardSide.FRONT));

        assertEquals(0, playerHand.getCards().size());
        assertEquals(tempSize - 1, playerHand.getCards().size());

        assertEquals(card, playerBoard.getCard(new Coords(1, 1)));

        assertEquals(CornerTypes.COVERED, playerBoard.getCard(new Coords(0,0)).getActiveCorners().get(CornerPosition.TOP_RIGHT).getType());
    }

    @Test
    void drawResourceDeckCard() {
        // hand should be empty
        assertTrue(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().isEmpty());


        // hand should contain the drawn cards until size 3 of playerHand is reached
        gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
        assertEquals(1, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
        assertEquals(2, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
        assertEquals(3, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        int tempSize = model.getResourceCardsDeck().size();
        assertFalse(gameModelUpdater.drawResourceDeckCard(PlayerToken.RED));    // hand is full, so no card should be drawn
        assertEquals(3, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());  // hand size should not change
        assertEquals(tempSize, model.getResourceCardsDeck().size());    // deck size should not change, as hand is full and no card should be drawn


        // emptying deck
        for(int i = 0; !model.getResourceCardsDeck().isEmpty(); i++) {
            gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);

            model.tokenToPlayer.get(PlayerToken.GREEN).getHand().getCards().forEach(
                    card -> model.tokenToPlayer.get(PlayerToken.GREEN).getHand().removeCard(card)
                );
        }

        ArrayList<PlayableCard> tempCards = new ArrayList<>(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards());

        // draw should not be completed as deck is empty
        assertFalse(gameModelUpdater.drawResourceDeckCard(PlayerToken.RED));
        // hand should not be changed since deck is empty
        assertTrue(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().containsAll(tempCards) && tempCards.containsAll(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards()));
    }

    @Test
    void drawGoldDeckCard() {
        // following tests are exactly the same as the previous test method. Implemented for completeness
        assertTrue(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().isEmpty());


        gameModelUpdater.drawGoldDeckCard(PlayerToken.RED);
        assertEquals(1, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        gameModelUpdater.drawGoldDeckCard(PlayerToken.RED);
        assertEquals(2, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        gameModelUpdater.drawGoldDeckCard(PlayerToken.RED);
        assertEquals(3, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        int tempSize = model.getGoldCardsDeck().size();
        assertFalse(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));
        assertEquals(3, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());
        assertEquals(tempSize, model.getGoldCardsDeck().size());


        for(int i = 0; !model.getGoldCardsDeck().isEmpty(); i++) {
            gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);

            model.tokenToPlayer.get(PlayerToken.GREEN).getHand().getCards().forEach(
                    card -> model.tokenToPlayer.get(PlayerToken.GREEN).getHand().removeCard(card)
            );
        }

        ArrayList<PlayableCard> tempCards = new ArrayList<>(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards());

        assertFalse(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));
        assertTrue(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().containsAll(tempCards) && tempCards.containsAll(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards()));
    }

    @Test
    void drawVisibleResourceCard() {
        // hand should be empty
        assertTrue(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().isEmpty());

        // there should be 2 visible cards
        assertEquals(2, model.getVisibleResourceCards().size());

        // drawCard should be successful and card should be added to hand
        assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 0));
        assertEquals(1, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());
        assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 1));
        assertEquals(2, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        // cards drawn into player's hand must not be in the visibleCards list
        model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards()
                .forEach(card -> assertFalse(model.getVisibleResourceCards().contains((ResourceCard) card)));

        // amount of visible cards should still be 2 as the deck is not empty
        assertEquals(2, model.getVisibleResourceCards().size());

        // emptying deck for further tests
        for(int i = 0; !model.getResourceCardsDeck().isEmpty(); i++) {
            gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawResourceDeckCard(PlayerToken.GREEN);

            model.tokenToPlayer.get(PlayerToken.GREEN).getHand().getCards().forEach(
                    card -> model.tokenToPlayer.get(PlayerToken.GREEN).getHand().removeCard(card)
            );
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
    void drawVisibleGoldCard() {
        // following tests are exactly the same as the previous test method. Implemented for completeness
        assertTrue(model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().isEmpty());

        assertEquals(2, model.getVisibleGoldCards().size());

        assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 0));
        assertEquals(1, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());
        assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));
        assertEquals(2, model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards().size());

        model.tokenToPlayer.get(PlayerToken.RED).getHand().getCards()
                .forEach(card -> assertFalse(model.getVisibleGoldCards().contains((GoldCard) card)));

        assertEquals(2, model.getVisibleGoldCards().size());

        for(int i = 0; !model.getGoldCardsDeck().isEmpty(); i++) {
            gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);
            gameModelUpdater.drawGoldDeckCard(PlayerToken.GREEN);

            model.tokenToPlayer.get(PlayerToken.GREEN).getHand().getCards().forEach(
                    card -> model.tokenToPlayer.get(PlayerToken.GREEN).getHand().removeCard(card)
            );
        }

        assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));
        assertFalse(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));

        assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.YELLOW, 0));
        assertFalse(gameModelUpdater.drawVisibleGoldCard(PlayerToken.YELLOW, 0));
    }

    @Test
    void addPlayer() {
        assertFalse(gameModelUpdater.addPlayer(PlayerToken.RED, starterCard.get(), CardSide.FRONT, objectiveCard.get()));

        assertTrue(gameModelUpdater.addPlayer(PlayerToken.BLUE, starterCard.get(), CardSide.FRONT, objectiveCard.get()));
        assertFalse(gameModelUpdater.addPlayer(PlayerToken.BLUE, starterCard.get(), CardSide.FRONT, objectiveCard.get()));
    }
}