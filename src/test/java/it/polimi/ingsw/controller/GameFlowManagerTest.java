package it.polimi.ingsw.controller;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.game.*;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameFlowManagerTest {
  private UserInfo creator;
  private UserInfo player;
  private Lobby lobby;

  private List<it.polimi.ingsw.controller.observer.Observer> observers;
  private Map<UserInfo, AtomicBoolean> isConnected;

  private GameFlowManager gameFlowManager;
  private GameModelUpdater gameModelUpdater;
  private GameModel gameModel;

  @BeforeEach
  void setUp() {
    observers = new ArrayList<>();

    User creatorUser = new User("Andrea");
    creator = new UserInfo(creatorUser);

    User playerUser = new User("Maradona");
    player = new UserInfo(playerUser);

    lobby = new Lobby(creatorUser);
    lobby.addUser(new User(player.name));

    isConnected = new HashMap<UserInfo, AtomicBoolean>() {{
      put(creator, new AtomicBoolean(true));
      put(player, new AtomicBoolean(true));
    }};
  }

  @Test
  void gameFlowManagerConstructorTest() {
    gameFlowManager = new GameFlowManager(lobby, isConnected, observers);
    gameModelUpdater = gameFlowManager.gameModelUpdater;

    assertNotNull(gameFlowManager.userInfoToToken);
    assertEquals(0, gameFlowManager.userInfoToToken.size());
    
    assertNotNull(gameFlowManager.playerTokens);
    assertEquals(0, gameFlowManager.playerTokens.size());

    assertEquals(gameFlowManager.tokenSelectionState, gameFlowManager.currentState);
  }

  @Test
  void gameInitializationTest() {
    gameFlowManager = new GameFlowManager(lobby, isConnected, observers, 1);
    gameModelUpdater = gameFlowManager.gameModelUpdater;

    Thread gameFlowManagerThread = new Thread(gameFlowManager);
    gameFlowManagerThread.start();
    try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }

    gameFlowManager.addCommand(new SelectTokenCommand(creator, PlayerToken.RED));
    try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }

    gameFlowManager.addCommand(new SelectTokenCommand(player, PlayerToken.RED));    
    // gameFlowManager.addCommand(new SelectTokenCommand(player, PlayerToken.BLUE));    
    try { Thread.sleep(1100); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(gameFlowManager.starterCardSelectionState, gameFlowManager.currentState);
    assertEquals(2, gameFlowManager.playerTokens.size());

    // gameFlowManager.addCommand(new DrawStarterCardCommand(PlayerToken.RED));
    gameFlowManager.addCommand(new DrawStarterCardCommand(PlayerToken.BLUE));
    gameFlowManager.addCommand(new DrawStarterCardCommand(PlayerToken.BLUE));
    try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
  
    assertEquals(gameFlowManager.starterCardSelectionState, gameFlowManager.currentState); 
  
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(PlayerToken.BLUE, CardSide.FRONT));
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(PlayerToken.BLUE, CardSide.FRONT));
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(PlayerToken.RED, CardSide.BACK));

    try { Thread.sleep(1100); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(gameFlowManager.objectiveCardSelectionState, gameFlowManager.currentState); 

    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(PlayerToken.BLUE));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.BLUE, 0));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.RED, 0));
    try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(gameFlowManager.objectiveCardSelectionState, gameFlowManager.currentState);

    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(PlayerToken.RED));
    // gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.RED, 1));
    try { Thread.sleep(1100); } catch (InterruptedException e) { e.printStackTrace(); }
    
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);
  
    assertNull(gameModelUpdater);
    gameModelUpdater = gameFlowManager.gameModelUpdater;
    gameModel = gameModelUpdater.gameModel;
    assertNotNull(gameModelUpdater);
    assertNotNull(gameModel);

    assertTrue(gameFlowManager.playerTokens.stream().allMatch(x -> gameModel.tokenToPlayer.get(x).secretObjective != null));
    assertTrue(gameFlowManager.playerTokens.stream().allMatch(x -> gameModel.tokenToPlayer.get(x).playerBoard.board.get(new Coords(0,0)) != null));
    assertTrue(gameFlowManager.playerTokens.stream().allMatch(x -> gameModel.tokenToPlayer.get(x).playerHand.size() == 3));
  }

  @Test
  void gameTest() {
    gameFlowManager = new GameFlowManager(lobby, isConnected, observers);

    Thread gameFlowManagerThread = new Thread(gameFlowManager);
    gameFlowManagerThread.start();
    try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }

    gameFlowManager.addCommand(new SelectTokenCommand(creator, PlayerToken.RED));
    gameFlowManager.addCommand(new SelectTokenCommand(player, PlayerToken.BLUE));    

    gameFlowManager.addCommand(new DrawStarterCardCommand(PlayerToken.RED));
    gameFlowManager.addCommand(new DrawStarterCardCommand(PlayerToken.BLUE));
  
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(PlayerToken.BLUE, CardSide.BACK));
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(PlayerToken.RED, CardSide.BACK));

    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(PlayerToken.BLUE));
    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(PlayerToken.RED));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.BLUE, 0));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.RED, 1));

    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
    
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);


    PlayerToken firstPlayerToken = gameFlowManager.getTurn();
    PlayerToken secondPlayerToken = gameFlowManager.playerTokens.stream().filter(pt -> pt != firstPlayerToken).findFirst().orElseThrow();

    /*// Corners that will be used by a specific card
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
    centralElements.insert(Resources.FUNGI);
    centralElements.insert(Resources.INSECT);

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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
            .getBoard()
            .getCard(new Coords(1, 1)));
    gameFlowManager
        .gameModelUpdater
        .getPlayers()
        .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
        .setPlayerHand(new PlayerHand()); // emptying player's hand, otherwise drawCard fails
    gameFlowManager
        .gameModelUpdater
        .getPlayers()
        .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
            .getBoard()
            .getCard(new Coords(-1, 1)));

    // card drawn should be added to firstPlayer's hand
    assertEquals(
        1,
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
            .getHand()
            .getCards()
            .size());

    // firstPlayer should not be able to draw the card as his time has expired
    int tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
            .getHand()
            .getCards()
            .size());

    // setting firstPlayer's score to a number above the points limit, which should trigger the post
    // game phase
    gameFlowManager.gameModelUpdater.setScoreTrack(
        Arrays.asList(
            gameFlowManager.UserInfoToToken.draw(firstPlayer),
            gameFlowManager.UserInfoToToken.draw(secondPlayer)));
    gameFlowManager
        .gameModelUpdater
        .getModel()
        .getScoreTrack()
        .updatePlayerScore(gameFlowManager.UserInfoToToken.draw(firstPlayer), 23);

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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
            .getBoard()
            .getCard(new Coords(1, 1)));

    gameFlowManager
        .gameModelUpdater
        .getPlayers()
        .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
        .setPlayerHand(new PlayerHand()); // emptying player's hand, otherwise drawCard fails
    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
            .getBoard()
            .getCard(new Coords(-1, -1)));

    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
            .getHand()
            .getCards()
            .size());

    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
            .getBoard()
            .getCard(new Coords(2, 2)));

    tempSize =
        gameFlowManager
            .gameModelUpdater
            .getPlayers()
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(firstPlayer))
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
            .draw(gameFlowManager.UserInfoToToken.draw(secondPlayer))
            .getBoard()
            .getCard(new Coords(1, 3)));
  */}
}