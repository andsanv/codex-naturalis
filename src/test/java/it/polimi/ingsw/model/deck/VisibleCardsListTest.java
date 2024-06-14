package it.polimi.ingsw.model.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.player.PlayerToken;

public class VisibleCardsListTest {
    List<Observer> observers;
    AtomicInteger lastEventId;
    Deck<ResourceCard> resourceCardDeck;

    PlayerToken playerToken;
    int handIndex;

    @BeforeEach
    void setUp() {
        observers = new ArrayList<Observer>();
        lastEventId = new AtomicInteger(0);
        resourceCardDeck = ResourceDeckCreator.createDeck(observers, lastEventId);
    
        playerToken = PlayerToken.RED;
        handIndex = 0;
    }
    
    @Test
    void drawTest() {
        VisibleCardsList<ResourceCard> visibleCardsList = new VisibleCardsList<>(resourceCardDeck, observers, lastEventId);
        
        assertEquals(Optional.empty(), visibleCardsList.draw(playerToken, 2, handIndex));
        assertEquals(Optional.empty(), visibleCardsList.draw(playerToken, -7, handIndex));

        while (resourceCardDeck.size() > 0) {
            ResourceCard drawnCard = visibleCardsList.draw(playerToken, resourceCardDeck.size() % 2, handIndex).orElse(null);
            assertNotNull(drawnCard);
            assertNotEquals(drawnCard, visibleCardsList.get(resourceCardDeck.size() % 2));
        }

        assertNotEquals(null, visibleCardsList.get(1));
        assertNotEquals(null, visibleCardsList.get(0));
        
        assertNotEquals(Optional.empty(), visibleCardsList.draw(playerToken, 0, handIndex));
        assertNull(visibleCardsList.get(0));
        
        assertNotEquals(Optional.empty(), visibleCardsList.draw(playerToken, 1, handIndex));
        assertNull(visibleCardsList.get(1));
        
        assertEquals(Optional.empty(), visibleCardsList.draw(playerToken, 0, handIndex));
        assertEquals(Optional.empty(), visibleCardsList.draw(playerToken, 1, handIndex));
    }
}
