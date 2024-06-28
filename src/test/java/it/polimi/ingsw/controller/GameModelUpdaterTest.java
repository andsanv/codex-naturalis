package it.polimi.ingsw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.stream;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.ScoreTrack;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameModelUpdaterTest {
    private GameModel gameModel;
    private GameModelUpdater gameModelUpdater;
    private Decks decks;

    private List<PlayerToken> playerTokens = new ArrayList<>();

    private Map<PlayerToken, StarterCard> tokenToStarterCard = new HashMap<>();
    private Map<PlayerToken, CardSide> tokenToCardSide = new HashMap<>();
    private Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard = new HashMap<>();

    private List<ObjectiveCard> commonObjectives = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private AtomicInteger lastEventId = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        decks = new Decks(observers, lastEventId);

        playerTokens = new ArrayList<>(Arrays.asList(PlayerToken.RED, PlayerToken.BLUE));

        StarterCard firstStarterCard = new StarterCard(
                0,
                new HashSet<>(Arrays.asList(Resources.ANIMAL)),
                new HashMap<>() {
                    {
                        put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.HIDDEN));
                        put(CornerPosition.TOP_RIGHT,
                                new Corner(Resources.PLANT, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_LEFT,
                                new Corner(Resources.INSECT, CornerTypes.VISIBLE));
                    }
                },
                new HashMap<>() {
                    {
                        put(CornerPosition.TOP_LEFT,
                                new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
                        put(CornerPosition.TOP_RIGHT,
                                new Corner(Resources.PLANT, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_RIGHT,
                                new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_LEFT,
                                new Corner(Resources.INSECT, CornerTypes.VISIBLE));
                    }
                });

        StarterCard secondStarterCard = new StarterCard(
                1,
                new HashSet<>(Arrays.asList(Resources.FUNGI)),
                new HashMap<>() {
                    {
                        put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
                        put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_RIGHT,
                                new Corner(Resources.INSECT, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));
                    }
                },
                new HashMap<>() {
                    {
                        put(CornerPosition.TOP_LEFT,
                                new Corner(Resources.FUNGI, CornerTypes.VISIBLE));
                        put(CornerPosition.TOP_RIGHT,
                                new Corner(Resources.PLANT, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_RIGHT,
                                new Corner(Resources.ANIMAL, CornerTypes.VISIBLE));
                        put(CornerPosition.BOTTOM_LEFT,
                                new Corner(Resources.INSECT, CornerTypes.VISIBLE));
                    }
                });

        tokenToStarterCard.put(PlayerToken.RED, firstStarterCard);
        tokenToStarterCard.put(PlayerToken.BLUE, secondStarterCard);

        tokenToCardSide.put(PlayerToken.RED, CardSide.FRONT);
        tokenToCardSide.put(PlayerToken.BLUE, CardSide.BACK);

        tokenToObjectiveCard.put(PlayerToken.RED, decks.objectiveCardsDeck.anonymousDraw().first.get());
        tokenToObjectiveCard.put(PlayerToken.BLUE, decks.objectiveCardsDeck.anonymousDraw().first.get());

        commonObjectives.add(decks.objectiveCardsDeck.anonymousDraw().first.get());
        commonObjectives.add(decks.objectiveCardsDeck.anonymousDraw().first.get());

        gameModel = new GameModel(
                decks,
                playerTokens,
                tokenToStarterCard,
                tokenToCardSide,
                tokenToObjectiveCard,
                commonObjectives,
                observers,
                lastEventId);

        gameModelUpdater = new GameModelUpdater(gameModel);
    }

    @Test
    void playCardPlacementTest() {
        Player redPlayer = gameModel.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand redPlayerHand = redPlayer.playerHand;
        PlayerBoard redPlayerBoard = redPlayer.playerBoard;

        int oldId = redPlayerHand.getCards().get(0).id;
        if (redPlayerHand.getCards().get(0).enoughResources(redPlayerBoard.playerElements, CardSide.FRONT)) {
            assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1),
                    redPlayerHand.getCards().get(0).id,
                    CardSide.FRONT));
            assertNotNull(redPlayerBoard.getCard(new Coords(1, 1)));
            assertEquals(oldId, redPlayerBoard.getCard(new Coords(1, 1)).id);
            assertTrue(redPlayerHand.getCards().stream().filter(Objects::nonNull)
                    .noneMatch(x -> x.id == oldId));
        } else {
            assertFalse(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1),
                    redPlayerHand.getCards().get(0).id,
                    CardSide.FRONT));
            assertNull(redPlayerBoard.getCard(new Coords(1, 1)));
            assertFalse(redPlayerHand.getCards().stream().filter(Objects::nonNull)
                    .noneMatch(x -> x.id == oldId));
        }

        PlayableCard oldCard = redPlayerHand.getCards().get(2);
        assertFalse(gameModelUpdater.playCard(PlayerToken.RED, new Coords(-1, 1),
                redPlayerHand.getCards().get(2).id,
                CardSide.FRONT));
        assertTrue(redPlayerHand.getCards().contains(oldCard));

        PlayableCard resourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);

        assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(-1, -1), resourceCard.id,
                CardSide.FRONT));
        assertNotNull(redPlayerBoard.getCard(new Coords(-1, -1)));
        assertEquals(resourceCard.id, redPlayerBoard.getCard(new Coords(-1, -1)).id);
        assertTrue(redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .noneMatch(x -> x.id == resourceCard.id));

        PlayableCard lastCard = redPlayerHand.getCards().stream().filter(Objects::nonNull).findFirst()
                .orElse(null);
        if (lastCard.enoughResources(redPlayerBoard.playerElements, CardSide.FRONT)) {
            if (redPlayerBoard.getCard(new Coords(-1, -1)).getActiveCorners()
                    .get(CornerPosition.BOTTOM_RIGHT)
                    .canPlaceCardAbove()) {
                assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(0, -2), lastCard.id,
                        CardSide.FRONT));
                assertNotNull(redPlayerBoard.getCard(new Coords(0, -2)));
                assertEquals(lastCard.id, redPlayerBoard.getCard(new Coords(0, -2)).id);
                assertTrue(
                        redPlayerHand.getCards().stream().filter(Objects::nonNull)
                                .noneMatch(x -> x.id == lastCard.id));
            } else {
                assertFalse(gameModelUpdater.playCard(PlayerToken.RED, new Coords(0, -2), lastCard.id,
                        CardSide.FRONT));
                assertNull(redPlayerBoard.getCard(new Coords(0, -2)));
                assertFalse(
                        redPlayerHand.getCards().stream().filter(Objects::nonNull)
                                .noneMatch(x -> x.id == lastCard.id));
            }
        } else {
            assertFalse(gameModelUpdater.playCard(PlayerToken.RED, new Coords(0, -2), lastCard.id,
                    CardSide.FRONT));
            assertNull(redPlayerBoard.getCard(new Coords(0, -2)));
            assertFalse(redPlayerHand.getCards().stream().filter(Objects::nonNull)
                    .noneMatch(x -> x.id == lastCard.id));
        }
    }

    @Test
    void playCardPointsAndResourcesTest() {
        Player redPlayer = gameModel.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand redPlayerHand = redPlayer.playerHand;
        PlayerBoard redPlayerBoard = redPlayer.playerBoard;

        assertEquals(1, redPlayerBoard.playerElements.get(Resources.PLANT));
        assertEquals(1, redPlayerBoard.playerElements.get(Resources.INSECT));

        PlayableCard resourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), resourceCard.id, CardSide.FRONT);

        assertEquals(
                resourceCard.getActiveCorners().values().stream().map(x -> x.element)
                        .filter(Optional::isPresent)
                        .filter(x -> x.get() == Resources.PLANT).count(),
                (long) redPlayerBoard.playerElements.get(Resources.PLANT));
        assertEquals(
                resourceCard.getActiveCorners().values().stream().map(x -> x.element)
                        .filter(Optional::isPresent)
                        .filter(x -> x.get() == Resources.INSECT).count() + 1, // We have to account for the central resouce
                (long) redPlayerBoard.playerElements.get(Resources.INSECT));
        assertEquals(
                resourceCard.getActiveCorners().values().stream()
                .map(x -> x.element)
                        .filter(Optional::isPresent)
                        .filter(x -> x.get() == Resources.ANIMAL).count() + 1 , // Again, accounting for the central resouce
                (long) redPlayerBoard.playerElements.get(Resources.ANIMAL));
        assertEquals(
                resourceCard.getActiveCorners().values().stream().map(x -> x.element)
                        .filter(Optional::isPresent)
                        .filter(x -> x.get() == Resources.FUNGI).count(),
                (long) redPlayerBoard.playerElements.get(Resources.FUNGI));
    }

    @Test
    void computeCardsPlayabilityTest() {
        assertTrue(gameModelUpdater.computeCardsPlayability(PlayerToken.RED));
    }

    @Test
    void drawResourceDeckCardTest() {
        Player redPlayer = gameModel.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand redPlayerHand = redPlayer.playerHand;

        assertEquals(3, redPlayerHand.size());
        assertFalse(gameModelUpdater.drawResourceDeckCard(PlayerToken.RED));

        PlayableCard resourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), resourceCard.id,
                CardSide.FRONT));
        assertEquals(2, redPlayerHand.size());

        int oldDeckSize = decks.resourceCardsDeck.size();
        int firstFreeIndex = redPlayerHand.getFirstFree();
        assertNull(redPlayerHand.getCards().get(firstFreeIndex));
        assertTrue(gameModelUpdater.drawResourceDeckCard(PlayerToken.RED));
        assertEquals(3, redPlayerHand.size());
        assertNotNull(redPlayerHand.getCards().get(firstFreeIndex));
        assertEquals(oldDeckSize, decks.resourceCardsDeck.size() + 1);

        oldDeckSize = decks.resourceCardsDeck.size();
        assertFalse(gameModelUpdater.drawResourceDeckCard(PlayerToken.RED));
        assertEquals(oldDeckSize, decks.resourceCardsDeck.size());
    }

    @Test
    void drawGoldDeckCardTest() {
        Player redPlayer = gameModel.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand redPlayerHand = redPlayer.playerHand;

        assertEquals(3, redPlayerHand.size());
        assertFalse(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));

        PlayableCard resourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), resourceCard.id,
                CardSide.FRONT));
        assertEquals(2, redPlayerHand.size());

        int oldDeckSize = decks.goldCardsDeck.size();
        int firstFreeIndex = redPlayerHand.getFirstFree();
        assertNull(redPlayerHand.getCards().get(firstFreeIndex));
        assertTrue(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));
        assertEquals(3, redPlayerHand.size());
        assertNotNull(redPlayerHand.getCards().get(firstFreeIndex));
        assertEquals(oldDeckSize, decks.goldCardsDeck.size() + 1);

        oldDeckSize = decks.goldCardsDeck.size();
        assertFalse(gameModelUpdater.drawGoldDeckCard(PlayerToken.RED));
        assertEquals(oldDeckSize, decks.goldCardsDeck.size());
    }

    @Test
    void drawVisibleResourceCardTest() {
        Player redPlayer = gameModel.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand redPlayerHand = redPlayer.playerHand;

        assertEquals(3, redPlayerHand.size());
        PlayableCard oldCard = gameModel.visibleResourceCards.get(0);
        assertFalse(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 0));

        PlayableCard resourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), resourceCard.id,
                CardSide.FRONT));
        assertEquals(2, redPlayerHand.size());

        int firstFreeIndex = redPlayerHand.getFirstFree();
        assertNull(redPlayerHand.getCards().get(firstFreeIndex));
        assertTrue(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 0));
        assertEquals(3, redPlayerHand.size());
        assertEquals(oldCard, redPlayerHand.getCards().get(firstFreeIndex));
        assertEquals(2, gameModel.visibleResourceCards.getCards().size());

        oldCard = gameModel.visibleResourceCards.get(0);
        assertFalse(gameModelUpdater.drawVisibleResourceCard(PlayerToken.RED, 1));
        assertEquals(oldCard, gameModel.visibleResourceCards.get(0));
        assertEquals(2, gameModel.visibleResourceCards.getCards().size());
    }

    @Test
    void drawVisibleGoldCardTest() {
        PlayerHand redPlayerHand = gameModel.tokenToPlayer.get(PlayerToken.RED).playerHand;
        PlayerHand bluePlayerHand = gameModel.tokenToPlayer.get(PlayerToken.BLUE).playerHand;

        List<PlayableCard> blueCards = bluePlayerHand.getCards();

        assertEquals(3, redPlayerHand.size());
        PlayableCard oldCard = gameModel.visibleGoldCards.get(0);
        assertFalse(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 0));

        PlayableCard resourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        assertTrue(gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), resourceCard.id,
                CardSide.FRONT));
        assertEquals(2, redPlayerHand.size());

        int firstFreeIndex = redPlayerHand.getFirstFree();
        assertNull(redPlayerHand.getCards().get(firstFreeIndex));
        assertTrue(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 0));
        assertEquals(3, redPlayerHand.size());
        assertEquals(oldCard, redPlayerHand.getCards().get(firstFreeIndex));
        assertEquals(2, gameModel.visibleGoldCards.getCards().size());

        oldCard = gameModel.visibleGoldCards.get(0);
        assertFalse(gameModelUpdater.drawVisibleGoldCard(PlayerToken.RED, 1));
        assertEquals(oldCard, gameModel.visibleGoldCards.get(0));
        assertEquals(2, gameModel.visibleGoldCards.getCards().size());

        assertTrue(bluePlayerHand.getCards().containsAll(blueCards));
        assertTrue(blueCards.containsAll(bluePlayerHand.getCards()));
    }

    @Test
    void limitScoreReachedTest() {
        ScoreTrack scoreTrack = gameModel.scoreTrack;

        assertFalse(gameModelUpdater.limitScoreReached());
        scoreTrack.updatePlayerScore(PlayerToken.RED, 12);
        assertFalse(gameModelUpdater.limitScoreReached());

        scoreTrack.updatePlayerScore(PlayerToken.RED, 9);
        assertTrue(gameModelUpdater.limitScoreReached());

        scoreTrack.updatePlayerScore(PlayerToken.RED, -9);
        assertFalse(gameModelUpdater.limitScoreReached());

        scoreTrack.updatePlayerScore(PlayerToken.BLUE, 29);
        assertTrue(gameModelUpdater.limitScoreReached());
    }

    @Test
    void anyDeckEmptyTest() {
        PlayerHand redPlayerHand = gameModel.tokenToPlayer.get(PlayerToken.RED).playerHand;

        assertFalse(gameModelUpdater.anyDeckEmpty());

        while (decks.resourceCardsDeck.size() > 0) {
            redPlayerHand.remove(redPlayerHand.getCards().get(0));
            gameModelUpdater.drawResourceDeckCard(PlayerToken.RED);
        }

        assertTrue(gameModelUpdater.anyDeckEmpty());
    }

    @Test
    void getSlimGameModel() {
        Player redPlayer = gameModel.tokenToPlayer.get(PlayerToken.RED);
        PlayerHand redPlayerHand = redPlayer.playerHand;

        PlayableCard firstResourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, 1), firstResourceCard.id, CardSide.FRONT);

        PlayableCard secondResourceCard = redPlayerHand.getCards().stream().filter(Objects::nonNull)
                .filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
        gameModelUpdater.playCard(PlayerToken.RED, new Coords(1, -1), secondResourceCard.id, CardSide.FRONT);

        SlimGameModel slimGameModel = gameModelUpdater.getSlimGameModel();

        slimGameModel.tokenToPlayedCards.containsKey(PlayerToken.RED);
        assertEquals(3, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).size());

        slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).containsKey(0);
        assertEquals(gameModel.tokenToPlayer.get(PlayerToken.RED).playerBoard.board.get(new Coords(0, 0)).id,
                slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(0).first);
        assertEquals(
                gameModel.tokenToPlayer.get(PlayerToken.RED).playerBoard.board.get(new Coords(0, 0))
                        .getPlayedSide(),
                slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(0).second);
        assertEquals(new Coords(0, 0), slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(0).third);

        slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).containsKey(1);
        assertEquals(firstResourceCard.id, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).first);
        assertEquals(CardSide.FRONT, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).second);
        assertEquals(new Coords(1, 1), slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).third);

        slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).containsKey(2);
        assertEquals(secondResourceCard.id, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(2).first);
        assertEquals(CardSide.FRONT, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(2).second);
        assertEquals(new Coords(1, -1), slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(2).third);

        slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).containsKey(0);
        assertEquals(gameModel.tokenToPlayer.get(PlayerToken.BLUE).playerBoard.board.get(new Coords(0, 0)).id,
                slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(0).first);
        assertEquals(
                gameModel.tokenToPlayer.get(PlayerToken.BLUE).playerBoard.board.get(new Coords(0, 0))
                        .getPlayedSide(),
                slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(0).second);
        assertEquals(new Coords(0, 0), slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(0).third);
    }

    @Test
    void slimGameModelTest() {
        SlimGameModel slimGameModel = gameModelUpdater.getSlimGameModel();

        assertEquals(0, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(0).first);
        assertEquals(1, slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(0).first);

        assertEquals(1, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).size());
        assertEquals(1, slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).size());

        slimGameModel.applyUpdatedScoreTrackEvent(PlayerToken.RED, 5);
        assertEquals(5, slimGameModel.scores.get(PlayerToken.RED));
        assertEquals(0, slimGameModel.scores.get(PlayerToken.BLUE));
        slimGameModel.applyUpdatedScoreTrackEvent(PlayerToken.RED, 15);
        slimGameModel.applyUpdatedScoreTrackEvent(PlayerToken.BLUE, 2);
        assertEquals(15, slimGameModel.scores.get(PlayerToken.RED));
        assertEquals(2, slimGameModel.scores.get(PlayerToken.BLUE));

        Map<Elements, Integer> elements = new HashMap<>() {
            {
                put(Resources.ANIMAL, 3);
                put(Resources.PLANT, 1);
                put(Resources.INSECT, 5);
                put(Resources.FUNGI, 0);
                put(Items.MANUSCRIPT, 7);
                put(Items.QUILL, 9);
                put(Items.INKWELL, 1);
            }
        };

        slimGameModel.applyPlayerElementsEvent(PlayerToken.RED, elements);
        assertEquals(3, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Resources.ANIMAL));
        assertEquals(1, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Resources.PLANT));
        assertEquals(5, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Resources.INSECT));
        assertEquals(0, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Resources.FUNGI));
        assertEquals(7, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Items.MANUSCRIPT));
        assertEquals(9, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Items.QUILL));
        assertEquals(1, slimGameModel.tokenToElements.get(PlayerToken.RED).get(Items.INKWELL));
        assertNotSame(elements, slimGameModel.tokenToElements.get(PlayerToken.RED));

        int cardIdRed = slimGameModel.tokenToHand.get(PlayerToken.RED).get(1);
        slimGameModel.applyPlayedCardEvent(PlayerToken.RED, slimGameModel.tokenToHand.get(PlayerToken.RED).get(1), CardSide.FRONT, new Coords(7, 13));

        assertEquals(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).first, cardIdRed);
        assertEquals(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).second, CardSide.FRONT);
        assertEquals(slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).get(1).third, new Coords(7, 13));

        assertEquals(2, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).size());
        assertEquals(1, slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).size());

        int cardIdBlue = slimGameModel.tokenToHand.get(PlayerToken.BLUE).get(0);
        slimGameModel.applyPlayedCardEvent(PlayerToken.BLUE, slimGameModel.tokenToHand.get(PlayerToken.BLUE).get(0), CardSide.FRONT, new Coords(9, 15));

        assertEquals(cardIdBlue, slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(1).first);
        assertEquals(CardSide.FRONT, slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(1).second);
        assertEquals(new Coords(9, 15), slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).get(1).third);
        assertEquals(2, slimGameModel.tokenToPlayedCards.get(PlayerToken.RED).size());
        assertEquals(2, slimGameModel.tokenToPlayedCards.get(PlayerToken.BLUE).size());

        int topCardId = gameModel.goldCardsDeck.getNextCardId();
        int nextCardId =
        gameModel.goldCardsDeck.asListOfIds().get(gameModel.goldCardsDeck.size() -
        2);
        slimGameModel.applyDrawnGoldDeckCardEvent(PlayerToken.RED, topCardId, false,
        nextCardId, 2);
        
        assertEquals(topCardId,
        slimGameModel.tokenToHand.get(PlayerToken.RED).get(2));
        assertEquals(nextCardId, slimGameModel.goldDeck.getLast());
        
        topCardId = gameModel.resourceCardsDeck.getNextCardId();
        nextCardId =
        gameModel.resourceCardsDeck.asListOfIds().get(gameModel.resourceCardsDeck.size()
        - 2);
        gameModel.resourceCardsDeck.anonymousDraw();
        slimGameModel.applyDrawnResourceDeckCardEvent(PlayerToken.BLUE, topCardId,
        false, nextCardId, 0);
        assertEquals(topCardId,
        slimGameModel.tokenToHand.get(PlayerToken.BLUE).get(0));
        assertEquals(nextCardId, slimGameModel.resourceDeck.getLast());
        topCardId = gameModel.resourceCardsDeck.getNextCardId();
        nextCardId = gameModel.resourceCardsDeck.asListOfIds().get(gameModel.resourceCardsDeck.size() - 2);
        int initialCardId = gameModel.visibleResourceCards.get(0).id;
        slimGameModel.applyDrawnVisibleResourceCardEvent(PlayerToken.RED, 0,
                slimGameModel.visibleResourceCardsList.get(0), slimGameModel.resourceDeck.getLast(),
                false, slimGameModel.resourceDeck.get(slimGameModel.resourceDeck.size() - 2), 1);

        assertEquals(initialCardId, slimGameModel.tokenToHand.get(PlayerToken.RED).get(1));
        assertEquals(topCardId, slimGameModel.visibleResourceCardsList.get(0));

        assertEquals(nextCardId, slimGameModel.resourceDeck.get(gameModel.resourceCardsDeck.size() - 2));


        gameModel.goldCardsDeck.anonymousDraw();
        topCardId = gameModel.goldCardsDeck.getNextCardId();
        nextCardId = gameModel.goldCardsDeck.asListOfIds().get(gameModel.goldCardsDeck.size() - 2);
        initialCardId = gameModel.visibleGoldCards.get(1).id;
        slimGameModel.applyDrawnVisibleGoldCardEvent(PlayerToken.BLUE, 1,
                slimGameModel.visibleGoldCardsList.get(1), slimGameModel.goldDeck.getLast(), false,
                slimGameModel.goldDeck.get(slimGameModel.goldDeck.size() - 2), 2);

                assertEquals(initialCardId, slimGameModel.tokenToHand.get(PlayerToken.BLUE).get(2));
        assertEquals(topCardId, slimGameModel.visibleGoldCardsList.get(1));
        assertEquals(nextCardId, slimGameModel.goldDeck.get(gameModel.goldCardsDeck.size() - 2));
    }
}