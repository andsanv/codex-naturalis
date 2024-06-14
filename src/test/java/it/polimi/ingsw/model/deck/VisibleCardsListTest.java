package it.polimi.ingsw.model.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.player.PlayerToken;

public class VisibleCardsListTest {
    List<Observer> observers;
    AtomicInteger lastEventId;
    Deck<ResourceCard> resourceCardDeck;
    Deck<GoldCard> goldCardDeck;
    Deck<ObjectiveCard> objectiveCardDeck;

    PlayerToken playerToken;
    int handIndex;

    @BeforeEach
    void setUp() {
        observers = new ArrayList<Observer>();
        lastEventId = new AtomicInteger(0);
        resourceCardDeck = ResourceDeckCreator.createDeck(observers, lastEventId);
        goldCardDeck = GoldDeckCreator.createDeck(observers, lastEventId);

        
        playerToken = PlayerToken.RED;
        handIndex = 0;
    }
    
    @Test
    void drawTest() {
        VisibleCardsList<ResourceCard> visibleResourceCardsList = new VisibleCardsList<>(resourceCardDeck, observers, lastEventId);
        
        assertEquals(Optional.empty(), visibleResourceCardsList.draw(playerToken, 2, handIndex));
        assertEquals(Optional.empty(), visibleResourceCardsList.draw(playerToken, -7, handIndex));

        while (resourceCardDeck.size() > 0) {
            ResourceCard drawnCard = visibleResourceCardsList.draw(playerToken, resourceCardDeck.size() % 2, handIndex).orElse(null);
            assertNotNull(drawnCard);
            assertNotEquals(drawnCard, visibleResourceCardsList.get(resourceCardDeck.size() % 2));
        }

        assertNotEquals(null, visibleResourceCardsList.get(1));
        assertNotEquals(null, visibleResourceCardsList.get(0));
        
        assertNotEquals(Optional.empty(), visibleResourceCardsList.draw(playerToken, 0, handIndex));
        assertNull(visibleResourceCardsList.get(0));
        
        assertNotEquals(Optional.empty(), visibleResourceCardsList.draw(playerToken, 1, handIndex));
        assertNull(visibleResourceCardsList.get(1));
        
        assertEquals(Optional.empty(), visibleResourceCardsList.draw(playerToken, 0, handIndex));
        assertEquals(Optional.empty(), visibleResourceCardsList.draw(playerToken, 1, handIndex));
    
        VisibleCardsList<GoldCard> visibleGoldCardsList = new VisibleCardsList<>(goldCardDeck, observers, lastEventId);
        assertEquals(visibleGoldCardsList.get(0), visibleGoldCardsList.draw(playerToken, 0, handIndex).orElse(null));
    
        objectiveCardDeck = ObjectiveDeckCreator.createDeck(observers, lastEventId);
        VisibleCardsList<ObjectiveCard> objectiveCardsList = new VisibleCardsList<>(objectiveCardDeck, observers, lastEventId);
        assertThrows(RuntimeException.class, () -> objectiveCardsList.draw(playerToken, 0, 1));
    }

    @Test
    void getCardsTest() {
        VisibleCardsList<ResourceCard> visibleCardsList = new VisibleCardsList<>(resourceCardDeck, observers, lastEventId);
    
        assertEquals(visibleCardsList.get(0), visibleCardsList.getCards().get(0));
        assertEquals(visibleCardsList.get(1), visibleCardsList.getCards().get(1));
    }

    @Test
    void getTest() {
        VisibleCardsList<ResourceCard> visibleCardsList = new VisibleCardsList<>(resourceCardDeck, observers, lastEventId);

        assertNull(visibleCardsList.get(-1));
        assertNull(visibleCardsList.get(2));

        assertEquals(visibleCardsList.getCards().get(0), visibleCardsList.get(0));
        assertEquals(visibleCardsList.getCards().get(1), visibleCardsList.get(1));
    }
}
