package it.polimi.ingsw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.distributed.commands.game.*;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PointsType;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerHand;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GameFlowManagerTest {
  @Mock private Lobby mockLobby;

  private GameFlowManager gameFlowManager;
  private User user1, user2;
  private GameModelUpdater gameModelUpdater;

  @BeforeEach
  void init() {
    mockLobby = mock(Lobby.class);
    user1 = new User("Andrea");
    user2 = new User("Maradona");
    when(mockLobby.getUsers()).thenReturn(Arrays.asList(user1, user2));

    gameFlowManager = new GameFlowManager(mockLobby);
    gameModelUpdater = gameFlowManager.gameModelUpdater;
  }

  @Test
  void gameFlowManagerSetupTest() {
    assertEquals(2, gameFlowManager.getIsConnected().keySet().size());

    assertTrue(
        gameFlowManager.getIsConnected().values().stream().allMatch(value -> value.equals(false)));

    assertEquals(0, gameFlowManager.idToToken.size());
    assertEquals(gameFlowManager.setupState, gameFlowManager.getCurrentState());

    gameFlowManager.getCurrentState().setup();

    assertEquals(2, gameFlowManager.idToToken.keySet().size());
    assertEquals(2, gameFlowManager.gameModelUpdater.getPlayers().size());

    for (Player player : gameFlowManager.gameModelUpdater.getPlayers().values()) {
      assertEquals(3, player.getHand().getCards().size());
    }

    assertEquals(2, gameFlowManager.gameModelUpdater.getCommonObjectives().size());
    assertTrue(
        gameFlowManager.gameModelUpdater.getCommonObjectives().stream()
            .noneMatch(objective -> objective == null));

    assertEquals(gameFlowManager.getCurrentState(), gameFlowManager.playCardState);
  }

  @Test
  void gameFlowTest() {
    // Corners that will be used by a specific card
    Map<CornerPosition, Corner> frontCorners = new HashMap<>();
    Map<CornerPosition, Corner> backCorners = new HashMap<>();
    ;

    frontCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
    frontCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
    frontCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
    frontCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

    backCorners.put(CornerPosition.TOP_LEFT, new Corner(null, CornerTypes.VISIBLE));
    backCorners.put(CornerPosition.TOP_RIGHT, new Corner(null, CornerTypes.VISIBLE));
    backCorners.put(CornerPosition.BOTTOM_RIGHT, new Corner(null, CornerTypes.VISIBLE));
    backCorners.put(CornerPosition.BOTTOM_LEFT, new Corner(null, CornerTypes.VISIBLE));

    // used for the specific starter card
    Set<Resources> centralElements = new HashSet<>();
    centralElements.add(Resources.FUNGI);
    centralElements.add(Resources.INSECT);

    // the specific card
    ResourceCard resourceCard =
        new ResourceCard(0, Resources.INSECT, PointsType.ONE, frontCorners, backCorners);

    // to speed up test output
    gameFlowManager.setTimeLimit(2);

    // starting the game
    new Thread(gameFlowManager).start();

    // to allow flowManager to finish setup phase
    while (gameFlowManager.getCurrentState().equals(gameFlowManager.setupState)) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
    }

    // getting first and second player (they are decided randomly at gameFlowManager construction
    // time
    String firstPlayer = gameFlowManager.getTurn();
    String secondPlayer = firstPlayer.equals(user1.name) ? user2.name : user1.name;

    // firstPlayer's turn. He should be able to play the card
    assertEquals(gameFlowManager.playCardState, gameFlowManager.getCurrentState());
    assertEquals(gameFlowManager.getTurn(), firstPlayer);

    gameFlowManager.addCommand(
        new PlayCardCommand(firstPlayer, new Coords(1, 1), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }

    assertEquals(
        resourceCard,
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getBoard()
            .getCard(new Coords(1, 1)));
    gameFlowManager
        .gameModelUpdater
        .getPlayers()
        .get(gameFlowManager.idToToken.get(firstPlayer))
        .setPlayerHand(new PlayerHand()); // emptying player's hand, otherwise drawCard fails
    gameFlowManager
        .gameModelUpdater
        .getPlayers()
        .get(gameFlowManager.idToToken.get(firstPlayer))
        .getHand()
        .addCard(resourceCard);

    // still firstPlayer's turn. He should be able to draw the card
    assertEquals(gameFlowManager.getTurn(), firstPlayer);
    assertEquals(gameFlowManager.getCurrentState(), gameFlowManager.drawCardState);

    gameFlowManager.addCommand(new DrawVisibleResourceCardCommand(firstPlayer, 0));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }

    assertEquals(
        2,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size());
    assertEquals(secondPlayer, gameFlowManager.getTurn());

    // as the secondPlayer does not call a method within the limit time, his turn should be skipped
    try {
      Thread.sleep(2200);
    } catch (InterruptedException e) {
    }

    // should be firstPlayer's turn
    assertEquals(firstPlayer, gameFlowManager.getTurn());
    assertNotEquals(secondPlayer, gameFlowManager.getTurn());

    // second player should not be able to play a card, since it's not his turn
    gameFlowManager.addCommand(
        new PlayCardCommand(secondPlayer, new Coords(1, 1), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(firstPlayer, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.playCardState, gameFlowManager.getCurrentState());
    assertNull(
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getBoard()
            .getCard(new Coords(1, 1)));

    // firstPlayer should first play a card and then draw
    gameFlowManager.addCommand(new DrawResourceDeckCardCommand(firstPlayer));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        2,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size());
    assertEquals(gameFlowManager.playCardState, gameFlowManager.getCurrentState());
    gameFlowManager.addCommand(
        new PlayCardCommand(firstPlayer, new Coords(-1, 1), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        resourceCard,
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getBoard()
            .getCard(new Coords(-1, 1)));

    // card drawn should be added to firstPlayer's hand
    assertEquals(
        1,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size());

    // should fail as it's still not secondPlayer's turn, even though it is a DrawCard turn
    gameFlowManager.addCommand(
        new PlayCardCommand(secondPlayer, new Coords(-1, 1), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertNull(
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getBoard()
            .getCard(new Coords(-1, 1)));

    // firstPlayer exceeds the time limit
    try {
      Thread.sleep(2100);
    } catch (InterruptedException e) {
    }

    // as firstPlayer was AFK, it should now be secondPlayer's turn
    assertEquals(secondPlayer, gameFlowManager.getTurn());

    // as firstPlayer AFKed in the DrawCard, to keep consistency in the game model, a random card
    // should be added to his hand
    assertEquals(
        2,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size());

    // firstPlayer should not be able to draw the card as his time has expired
    int tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size();
    gameFlowManager.addCommand(new DrawGoldDeckCardCommand(firstPlayer));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        tempSize,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size());

    // setting firstPlayer's score to a number above the points limit, which should trigger the post
    // game phase
    gameFlowManager.gameModelUpdater.setScoreTrack(
        Arrays.asList(
            gameFlowManager.idToToken.get(firstPlayer),
            gameFlowManager.idToToken.get(secondPlayer)));
    gameFlowManager
        .gameModelUpdater
        .getModel()
        .getScoreTrack()
        .updatePlayerScore(gameFlowManager.idToToken.get(firstPlayer), 23);

    // should not be last round yet
    assertFalse(gameFlowManager.isLastRound);
    assertEquals(secondPlayer, gameFlowManager.getTurn());
    gameFlowManager.addCommand(
        new PlayCardCommand(secondPlayer, new Coords(1, 1), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        resourceCard,
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getBoard()
            .getCard(new Coords(1, 1)));

    gameFlowManager
        .gameModelUpdater
        .getPlayers()
        .get(gameFlowManager.idToToken.get(secondPlayer))
        .setPlayerHand(new PlayerHand()); // emptying player's hand, otherwise drawCard fails
    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getHand()
            .getCards()
            .size();
    assertEquals(0, tempSize);
    gameFlowManager.addCommand(new DrawGoldDeckCardCommand(secondPlayer));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        tempSize + 1,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getHand()
            .getCards()
            .size());

    // should now be last round
    assertTrue(gameFlowManager.isLastRound);

    gameFlowManager.addCommand(
        new PlayCardCommand(secondPlayer, new Coords(2, 2), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertNull(
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getBoard()
            .getCard(new Coords(2, 2)));

    gameFlowManager.addCommand(
        new PlayCardCommand(firstPlayer, new Coords(-1, -1), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        resourceCard,
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getBoard()
            .getCard(new Coords(-1, -1)));

    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getHand()
            .getCards()
            .size();
    gameFlowManager.addCommand(new DrawVisibleGoldCardCommand(secondPlayer, 1));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        tempSize,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getHand()
            .getCards()
            .size());

    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size();
    gameFlowManager.addCommand(new DrawVisibleGoldCardCommand(firstPlayer, 1));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        tempSize + 1,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getHand()
            .getCards()
            .size());

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }

    // last turn of the last round, should be secondPlayer's turn
    assertEquals(secondPlayer, gameFlowManager.getTurn());

    gameFlowManager.addCommand(
        new PlayCardCommand(secondPlayer, new Coords(2, 2), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        resourceCard,
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getBoard()
            .getCard(new Coords(2, 2)));

    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getHand()
            .getCards()
            .size();
    gameFlowManager.addCommand(new DrawVisibleGoldCardCommand(secondPlayer, 1));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertEquals(
        tempSize + 1,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getHand()
            .getCards()
            .size());

    // to allow flowManager to enter post-game phase
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
    }

    // current state should be postGame
    assertEquals(gameFlowManager.postGameState, gameFlowManager.getCurrentState());

    // no player should be able to make a move at this stage
    gameFlowManager.addCommand(
        new PlayCardCommand(firstPlayer, new Coords(2, 2), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertNull(
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(firstPlayer))
            .getBoard()
            .getCard(new Coords(2, 2)));

    gameFlowManager.addCommand(
        new PlayCardCommand(secondPlayer, new Coords(1, 3), resourceCard, CardSide.FRONT));
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    assertNull(
        gameModelUpdater
            .getPlayers()
            .get(gameFlowManager.idToToken.get(secondPlayer))
            .getBoard()
            .getCard(new Coords(1, 3)));
  }
}
