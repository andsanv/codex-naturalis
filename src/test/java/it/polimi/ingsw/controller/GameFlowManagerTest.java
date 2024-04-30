package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PointsType;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerHand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameFlowManagerTest {
    @Mock
    private Lobby mockLobby;

    private GameFlowManager gameFlowManager;
    private User user1, user2;


    @BeforeEach
    void init() {
        mockLobby = mock(Lobby.class);
        user1 = new User("Andrea");
        user2 = new User("Maradona");
        when(mockLobby.getUsers()).thenReturn(Arrays.asList(user1, user2));

        gameFlowManager = new GameFlowManager(mockLobby);
    }

    @Test
    void gameFlowManagerSetupTest() {
        assertEquals(2, gameFlowManager.getIsConnected().keySet().size());

        assertTrue(gameFlowManager.getIsConnected().values().stream().allMatch(value -> value.equals(false)));

        assertEquals(0, gameFlowManager.IdToToken.size());
        assertEquals(gameFlowManager.setupState, gameFlowManager.getCurrentState());

        gameFlowManager.getCurrentState().setup();

        assertEquals(2, gameFlowManager.IdToToken.keySet().size());
        assertEquals(2, gameFlowManager.gameModelUpdater.getPlayers().size());

        for(Player player : gameFlowManager.gameModelUpdater.getPlayers().values()) {
            assertEquals(3, player.getHand().getCards().size());
        }

        assertEquals(2, gameFlowManager.gameModelUpdater.getCommonObjectives().size());
        assertTrue(gameFlowManager.gameModelUpdater.getCommonObjectives().stream().noneMatch(objective -> objective == null));

        assertEquals(gameFlowManager.getCurrentState(), gameFlowManager.playCardState);
    }

    @Test
    void gameFlowTest() {
        Map<CornerPosition, Corner> frontCorners = new HashMap<>();
        Map<CornerPosition, Corner> backCorners = new HashMap<>();;

        frontCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        frontCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

        backCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
        backCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

        Set<Resources> centralElements = new HashSet<>();
        centralElements.add(Resources.FUNGI);
        centralElements.add(Resources.INSECT);

        ResourceCard resourceCard = new ResourceCard(0, Resources.INSECT, PointsType.ONE, frontCorners, backCorners);

        gameFlowManager.setTimeLimit(2);    // for testing purposes
        new Thread(gameFlowManager).start();

        // to be sure to wait flowManager to finish setup phase
        while(gameFlowManager.getCurrentState().equals(gameFlowManager.setupState)) {
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e) {}
        }

        String firstPlayer = gameFlowManager.getTurn();
        String secondPlayer = firstPlayer.equals(user1.name) ? user2.name : user1.name;

        assertTrue(gameFlowManager.playCard(firstPlayer, new Coords(1,1), resourceCard, CardSide.FRONT));
        gameFlowManager.gameModelUpdater.getPlayers().get(gameFlowManager.IdToToken.get(firstPlayer)).setPlayerHand(new PlayerHand()); // emptying player's hand, otherwise drawCard fails
        gameFlowManager.gameModelUpdater.getPlayers().get(gameFlowManager.IdToToken.get(firstPlayer)).getHand().addCard(resourceCard);
        assertTrue(gameFlowManager.drawVisibleResourceCard(firstPlayer, 0));

        try {
            Thread.sleep(2200);
        }
        catch(InterruptedException e) {}

        assertNotEquals(secondPlayer, gameFlowManager.getTurn());
        assertEquals(firstPlayer, gameFlowManager.getTurn());
        assertFalse(gameFlowManager.playCard(secondPlayer, new Coords(1,1), resourceCard, CardSide.FRONT));

        try {
            Thread.sleep(100);
        }
        catch(InterruptedException e) {}

        assertFalse(gameFlowManager.drawResourceDeckCard(firstPlayer));
        assertTrue(gameFlowManager.playCard(firstPlayer, new Coords(-1,1), resourceCard, CardSide.FRONT));

        assertEquals(1, gameFlowManager.gameModelUpdater.getPlayers().get(gameFlowManager.IdToToken.get(firstPlayer)).getHand().getCards().size());
        assertFalse(gameFlowManager.playCard(secondPlayer, new Coords(-1,1), resourceCard, CardSide.FRONT));

        try {
            Thread.sleep(2100);
        }
        catch(InterruptedException e) {}

        assertEquals(secondPlayer, gameFlowManager.getTurn());
        assertEquals(2, gameFlowManager.gameModelUpdater.getPlayers().get(gameFlowManager.IdToToken.get(firstPlayer)).getHand().getCards().size());
        assertFalse(gameFlowManager.drawGoldDeckCard(firstPlayer));

        gameFlowManager.gameModelUpdater.setScoreTrack(Arrays.asList(gameFlowManager.IdToToken.get(firstPlayer), gameFlowManager.IdToToken.get(secondPlayer)));
        gameFlowManager.gameModelUpdater.getModel().getScoreTrack().updatePlayerScore(gameFlowManager.IdToToken.get(firstPlayer), 23);

        assertFalse(gameFlowManager.isLastRound);
        assertEquals(secondPlayer, gameFlowManager.getTurn());
        assertTrue(gameFlowManager.playCard(secondPlayer, new Coords(1,1), resourceCard, CardSide.FRONT));

        gameFlowManager.gameModelUpdater.getPlayers().get(gameFlowManager.IdToToken.get(secondPlayer)).setPlayerHand(new PlayerHand()); // emptying player's hand, otherwise drawCard fails
        assertTrue(gameFlowManager.drawGoldDeckCard(secondPlayer));

        try {
            Thread.sleep(100);
        }
        catch(InterruptedException e) {}

        assertTrue(gameFlowManager.isLastRound);

        assertFalse(gameFlowManager.playCard(secondPlayer, new Coords(2,2), resourceCard, CardSide.FRONT));
        assertTrue(gameFlowManager.playCard(firstPlayer, new Coords(-1,-1), resourceCard, CardSide.FRONT));
        assertFalse(gameFlowManager.drawVisibleGoldCard(secondPlayer, 1));
        assertTrue(gameFlowManager.drawVisibleGoldCard(firstPlayer, 1));

        try {
            Thread.sleep(100);
        }
        catch(InterruptedException e) {}

        assertEquals(secondPlayer, gameFlowManager.getTurn());
        assertTrue(gameFlowManager.playCard(secondPlayer, new Coords(2,2), resourceCard, CardSide.FRONT));
        assertTrue(gameFlowManager.drawVisibleGoldCard(secondPlayer, 1));

        try {
            Thread.sleep(500);
        }
        catch(InterruptedException e) {}

        assertEquals(gameFlowManager.postGameState, gameFlowManager.getCurrentState());

        assertFalse(gameFlowManager.playCard(secondPlayer, new Coords(2,2), resourceCard, CardSide.FRONT));
    }
}