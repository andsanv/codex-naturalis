package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.game.PlayedCardEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.card.objective.PatternObjectiveStrategy;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public class SlimGameModelTest {
    private List<Observer> observers;
    private AtomicInteger lastEventId;
    private List<PlayerToken> playerTokens;
    private Map<PlayerToken, StarterCard> tokenToStarterCard;
    private Map<PlayerToken, CardSide> tokenToCardSide;
    private Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard;

    private GameModel gameModel;
    
    @Mock
    private StarterCard redStarterCard = mock(StarterCard.class);
    private StarterCard blueStarterCard = mock(StarterCard.class);
    
    private ObjectiveCard redObjectiveCard = mock(ObjectiveCard.class);
    private ObjectiveCard blueObjectiveCard = mock(ObjectiveCard.class);
    
    private ObjectiveCard firstCommonObjectiveCard = new ObjectiveCard(12, 1, mock(PatternObjectiveStrategy.class));
    private ObjectiveCard secondCommonObjectiveCard = new ObjectiveCard(43, 1, mock(PatternObjectiveStrategy.class));
    private List<ObjectiveCard> commonObjectives = new ArrayList<>(Arrays.asList(firstCommonObjectiveCard, secondCommonObjectiveCard));

    @BeforeEach
    void setUp() {
        playerTokens = new ArrayList<>(Arrays.asList(PlayerToken.RED, PlayerToken.BLUE));
        tokenToStarterCard = new HashMap<PlayerToken, StarterCard>() {{
            put(PlayerToken.RED, redStarterCard);
            put(PlayerToken.BLUE, blueStarterCard);
        }};

        tokenToCardSide = new HashMap<PlayerToken, CardSide>() {{
            put(PlayerToken.RED, CardSide.FRONT);
            put(PlayerToken.BLUE, CardSide.FRONT);
        }};

        tokenToObjectiveCard = new HashMap<PlayerToken, ObjectiveCard>() {{
            put(PlayerToken.RED, redObjectiveCard);
            put(PlayerToken.BLUE, blueObjectiveCard);
        }};

        gameModel = new GameModel(
            new Decks(observers, lastEventId),
            playerTokens,
            tokenToStarterCard,
            tokenToCardSide,
            tokenToObjectiveCard,
            commonObjectives,
            observers,
            lastEventId
        );
    }

    @Test
    void constructorTest() {
        SlimGameModel slimGameModel = gameModel.slimGameModel;

        assertNotNull(slimGameModel);
        assertNotNull(slimGameModel.tokenToPlayedCards);
        
        assertNotNull(slimGameModel.commonObjectives);
        assertNotEquals(slimGameModel.commonObjectives, commonObjectives);
        assertTrue(commonObjectives.stream().allMatch(x -> slimGameModel.commonObjectives.contains(x.id)));
        
        assertNotNull(slimGameModel.tokenTosecretObjective);
        assertTrue(slimGameModel.tokenTosecretObjective.entrySet().stream().allMatch(x -> gameModel.tokenToPlayer.get(x.getKey()).secretObjective.id == x.getValue()));

        assertNotNull(slimGameModel.scores);
        assertEquals(slimGameModel.scores, gameModel.scoreTrack.getScores());
        assertTrue(slimGameModel.scores.entrySet().stream().allMatch(x -> gameModel.scoreTrack.getScores().get(x.getKey()).equals(x.getValue())));
        
        assertNotNull(slimGameModel.tokenToItems);
        assertTrue(slimGameModel.tokenToItems.entrySet().stream().allMatch(x -> gameModel.tokenToPlayer.get(x.getKey()).playerBoard.playerElements.equals(x.getValue())));
    
        assertNotNull(slimGameModel.tokenToPlayedCards);
        assertTrue(slimGameModel.tokenToPlayedCards.keySet().containsAll(playerTokens));
        assertTrue(playerTokens.containsAll(slimGameModel.tokenToPlayedCards.keySet()));
        assertTrue(playerTokens.stream().allMatch(x -> slimGameModel.tokenToPlayedCards.get(x).containsKey(0)));
        assertTrue(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(0).first.equals(redStarterCard.id));
        assertTrue(slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(0).first.equals(blueStarterCard.id));
    }

    @Test
    void copyConstructorTest() {
        SlimGameModel otherSlimGameModel = gameModel.slimGameModel;
        SlimGameModel slimGameModel = new SlimGameModel(otherSlimGameModel);

        assertNotSame(otherSlimGameModel.commonObjectives, slimGameModel.commonObjectives);
        assertEquals(otherSlimGameModel.commonObjectives, slimGameModel.commonObjectives);
    
        assertNotSame(otherSlimGameModel.scores, slimGameModel.scores);
        assertEquals(otherSlimGameModel.scores, slimGameModel.scores);

        assertNotSame(otherSlimGameModel.tokenTosecretObjective, slimGameModel.tokenTosecretObjective);
        assertEquals(otherSlimGameModel.tokenTosecretObjective, slimGameModel.tokenTosecretObjective);
    
        assertNotSame(slimGameModel.tokenToItems, otherSlimGameModel.tokenToItems);
        assertEquals(otherSlimGameModel.tokenToItems, slimGameModel.tokenToItems);
        assertTrue(slimGameModel.tokenToItems.entrySet().stream().noneMatch(
                x -> x.getValue() == otherSlimGameModel.tokenToItems.get(x.getKey())
        ));

        assertNotSame(slimGameModel.tokenToPlayedCards, otherSlimGameModel.tokenToPlayedCards);
        assertEquals(otherSlimGameModel.tokenToPlayedCards, slimGameModel.tokenToPlayedCards);
        assertTrue(slimGameModel.tokenToPlayedCards.entrySet().stream().noneMatch(
                x -> x.getValue() == otherSlimGameModel.tokenToPlayedCards.get(x.getKey()) && x.getValue().entrySet().stream().noneMatch(
                    y -> y.getValue() == otherSlimGameModel.tokenToPlayedCards.get(x.getKey()).get(y.getKey())
                )
        ));
    }

    @Test
    void addPlayedCardTest() {
        SlimGameModel slimGameModel = gameModel.slimGameModel;
 
        slimGameModel.addPlayedCard(
            new PlayedCardEvent(PlayerToken.RED, 77, CardSide.BACK, new Coords(4,5))
        );

        assertTrue(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).containsKey(1));
        assertTrue(
            slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).first == 77 &&
            slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).second == CardSide.BACK &&
            slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).third.equals(new Coords(4, 5))
        );

        slimGameModel.addPlayedCard(
            new PlayedCardEvent(PlayerToken.BLUE, 4, CardSide.FRONT, new Coords(16,0))
        );

        assertTrue(slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).containsKey(1));
        assertTrue(
            slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(1).first == 4 &&
            slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(1).second == CardSide.FRONT &&
            slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(1).third.equals(new Coords(16, 0))
        );

        slimGameModel.addPlayedCard(
            new PlayedCardEvent(PlayerToken.BLUE, 4, CardSide.FRONT, new Coords(16,0))
        );

        assertTrue(slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).containsKey(2));
        assertTrue(
            slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(2).first == 4 &&
            slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(2).second == CardSide.FRONT &&
            slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(2).third.equals(new Coords(16, 0))
        );

        assertFalse(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).containsKey(2));
        assertTrue(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).containsKey(0));
    }
}
