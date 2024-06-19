package it.polimi.ingsw.controller;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.game.*;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.model.player.PlayerHand;
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

  @BeforeEach
  void setUp() {
    observers = new ArrayList<>();

    User creatorUser = new User("Andrea");
    creator = new UserInfo(creatorUser);

    User playerUser = new User("Maradona");
    player = new UserInfo(playerUser);

    lobby = new Lobby(creatorUser);
    lobby.addUser(playerUser);

    isConnected = new HashMap<UserInfo, AtomicBoolean>() {{
      put(creator, new AtomicBoolean(true));
      put(player, new AtomicBoolean(true));
    }};
  }

  @Test
  void gameFlowManagerConstructorTest() {
    GameFlowManager gameFlowManager;

    gameFlowManager = new GameFlowManager(lobby, isConnected, observers);

    assertNotNull(gameFlowManager.userInfoToToken);
    assertEquals(0, gameFlowManager.userInfoToToken.size());
    
    assertNotNull(gameFlowManager.playerTokens);
    assertEquals(0, gameFlowManager.playerTokens.size());

    assertEquals(gameFlowManager.tokenSelectionState, gameFlowManager.currentState);
  }

  @Test
  void gameInitializationTest() {
    GameFlowManager gameFlowManager;
    GameModelUpdater gameModelUpdater;
    GameModel gameModel;

    gameFlowManager = new GameFlowManager(lobby, isConnected, observers, 3);
    gameModelUpdater = gameFlowManager.gameModelUpdater;

    PlayerToken firstPlayerToken = PlayerToken.RED;

    Thread gameFlowManagerThread = new Thread(gameFlowManager);
    gameFlowManagerThread.start();
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    gameFlowManager.addCommand(new SelectTokenCommand(creator, PlayerToken.RED));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    gameFlowManager.addCommand(new SelectTokenCommand(player, PlayerToken.RED));    
    // gameFlowManager.addCommand(new SelectTokenCommand(player, PlayerToken.BLUE));    
    try { Thread.sleep(3100); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(gameFlowManager.starterCardSelectionState, gameFlowManager.currentState);
    gameFlowManager.currentState.handleTokenSelection(new ArrayList<>());
    assertEquals(2, gameFlowManager.playerTokens.size());
    gameFlowManager.addCommand(new SelectTokenCommand(player, PlayerToken.RED));    

    PlayerToken secondPlayerToken = gameFlowManager.playerTokens.stream().filter(x -> !x.equals(firstPlayerToken)).findFirst().orElseThrow();

    // gameFlowManager.addCommand(new DrawStarterCardCommand(PlayerToken.RED));
    gameFlowManager.addCommand(new DrawStarterCardCommand(secondPlayerToken));
    gameFlowManager.addCommand(new DrawStarterCardCommand(secondPlayerToken));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
  
    assertEquals(gameFlowManager.starterCardSelectionState, gameFlowManager.currentState); 
  
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(secondPlayerToken, CardSide.FRONT));
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(secondPlayerToken, CardSide.FRONT));
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(PlayerToken.RED, CardSide.BACK));

    try { Thread.sleep(3100); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(gameFlowManager.objectiveCardSelectionState, gameFlowManager.currentState); 
    gameFlowManager.currentState.handleStarterCardSelection();

    gameFlowManager.addCommand(new DrawStarterCardCommand(secondPlayerToken));
    gameFlowManager.addCommand(new SelectStarterCardSideCommand(secondPlayerToken, CardSide.FRONT));

    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(secondPlayerToken));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(secondPlayerToken, 0));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.RED, 0));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(gameFlowManager.objectiveCardSelectionState, gameFlowManager.currentState);

    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(PlayerToken.RED));
    // gameFlowManager.addCommand(new SelectObjectiveCardCommand(PlayerToken.RED, 1));
    try { Thread.sleep(3100); } catch (InterruptedException e) { e.printStackTrace(); }
    
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);
    
    gameFlowManager.currentState.handleObjectiveCardSelection();
    gameFlowManager.currentState.handleInitialization(null, null, null, null);
    gameFlowManager.addCommand(new DrawObjectiveCardsCommand(secondPlayerToken));
    gameFlowManager.addCommand(new SelectObjectiveCardCommand(secondPlayerToken, 0));
    try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

    assertNull(gameModelUpdater);
    gameModelUpdater = gameFlowManager.gameModelUpdater;
    gameModel = gameModelUpdater.gameModel;
    assertNotNull(gameModelUpdater);
    assertNotNull(gameModel);

    assertTrue(gameFlowManager.playerTokens.stream().allMatch(x -> gameModel.tokenToPlayer.get(x).secretObjective != null));
    assertTrue(gameFlowManager.playerTokens.stream().allMatch(x -> gameModel.tokenToPlayer.get(x).playerBoard.board.get(new Coords(0,0)) != null));
    assertTrue(gameFlowManager.playerTokens.stream().allMatch(x -> gameModel.tokenToPlayer.get(x).playerHand.size() == 3));
    assertTrue(gameModel.tokenToPlayer.get(secondPlayerToken).playerBoard.getCard(new Coords(0,0)).getPlayedSide().equals(CardSide.FRONT));
  }

  @Test
  void gameTest() {
    GameFlowManager gameFlowManager;
    GameModelUpdater gameModelUpdater;
    GameModel gameModel;

    System.out.println("observers = " + observers);
    System.out.println("is connected = " + isConnected);

    gameFlowManager = new GameFlowManager(lobby, isConnected, observers, 3);

    Thread gameFlowManagerThread = new Thread(gameFlowManager);
    gameFlowManagerThread.start();
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    assertNull(gameFlowManager.getTurn());

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

    try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
    
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);

    gameModelUpdater = gameFlowManager.gameModelUpdater;
    gameModel = gameModelUpdater.gameModel;

    PlayerToken firstPlayerToken = gameFlowManager.getTurn();
    PlayerToken secondPlayerToken = gameFlowManager.playerTokens.stream().filter(pt -> pt != firstPlayerToken).findFirst().orElseThrow();

    
    assertEquals(CardSide.BACK, gameModel.tokenToPlayer.get(firstPlayerToken).playerBoard.getCard(new Coords(0,0)).getPlayedSide());
    assertEquals(CardSide.BACK, gameModel.tokenToPlayer.get(secondPlayerToken).playerBoard.getCard(new Coords(0,0)).getPlayedSide());

    Player firstPlayer = gameModel.tokenToPlayer.get(firstPlayerToken);
    PlayerHand firstPlayerHand = firstPlayer.playerHand;

    Player secondPlayer = gameModel.tokenToPlayer.get(secondPlayerToken);
    PlayerHand secondPlayerHand = secondPlayer.playerHand;
    PlayerBoard secondPlayerBoard = secondPlayer.playerBoard;

    PlayableCard resourceCard = firstPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(firstPlayerToken, new Coords(1,1), resourceCard.id, CardSide.FRONT));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(gameFlowManager.drawCardState, gameFlowManager.currentState);
    assertEquals(firstPlayerToken, gameFlowManager.getTurn());
    assertEquals(0, gameFlowManager.getRound());

    gameFlowManager.addCommand(new DrawResourceDeckCardCommand(firstPlayerToken));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertEquals(1, gameFlowManager.turn);
    assertEquals(0, gameFlowManager.getRound());
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);

    resourceCard = secondPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(secondPlayerToken, new Coords(0,0), resourceCard.id, CardSide.FRONT));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);

    gameFlowManager.addCommand(new PlayCardCommand(secondPlayerToken, new Coords(1,-1), resourceCard.id, CardSide.FRONT));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.drawCardState, gameFlowManager.currentState);

    resourceCard = secondPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(ResourceCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(secondPlayerToken, new Coords(-1,-1), resourceCard.id, CardSide.FRONT));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertNull(secondPlayerBoard.getCard(new Coords(-1,-1)));
    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.drawCardState, gameFlowManager.currentState);

    gameFlowManager.addCommand(new DrawResourceDeckCardCommand(firstPlayerToken));
    gameFlowManager.addCommand(new DrawGoldDeckCardCommand(firstPlayerToken));
    gameFlowManager.addCommand(new DrawVisibleResourceCardCommand(firstPlayerToken, 1));
    gameFlowManager.addCommand(new DrawVisibleGoldCardCommand(firstPlayerToken, 1));

    gameFlowManager.addCommand(new DrawVisibleResourceCardCommand(secondPlayerToken, 1));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(firstPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);
    assertEquals(2, gameFlowManager.turn);
    assertEquals(1, gameFlowManager.getRound());

    gameFlowManager.addCommand(new DrawResourceDeckCardCommand(firstPlayerToken));
    gameFlowManager.addCommand(new DrawGoldDeckCardCommand(firstPlayerToken));
    gameFlowManager.addCommand(new DrawVisibleResourceCardCommand(firstPlayerToken, 1));
    gameFlowManager.addCommand(new DrawVisibleGoldCardCommand(firstPlayerToken, 1));

    PlayableCard goldCard = firstPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(GoldCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(firstPlayerToken, new Coords(1,-1), goldCard.id, CardSide.BACK));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(firstPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.drawCardState, gameFlowManager.currentState);

    gameFlowManager.addCommand(new DrawGoldDeckCardCommand(firstPlayerToken));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);

    gameFlowManager.addCommand(new PlayCardCommand(firstPlayerToken, new Coords(1,-1), goldCard.id, CardSide.BACK));
    goldCard = secondPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(GoldCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(secondPlayerToken, new Coords(1,1), goldCard.id, CardSide.BACK));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertEquals(gameFlowManager.drawCardState, gameFlowManager.currentState);

    gameModel.scoreTrack.updatePlayerScore(firstPlayerToken, 25);

    gameFlowManager.addCommand(new DrawVisibleGoldCardCommand(secondPlayerToken, 1));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    assertEquals(firstPlayerToken, gameFlowManager.getTurn());
    assertTrue(gameFlowManager.isLastRound);
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);

    goldCard = firstPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(GoldCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(firstPlayerToken, new Coords(-1,1), goldCard.id, CardSide.BACK));
    try { Thread.sleep(3100); } catch (InterruptedException e) { e.printStackTrace(); }

    assertEquals(secondPlayerToken, gameFlowManager.getTurn());
    assertTrue(gameFlowManager.isLastRound);
    assertEquals(gameFlowManager.playCardState, gameFlowManager.currentState);

    goldCard = secondPlayerHand.getCards().stream().filter(Objects::nonNull).filter(x -> x.getClass().equals(GoldCard.class)).findFirst().orElse(null);
    gameFlowManager.addCommand(new PlayCardCommand(secondPlayerToken, new Coords(-1,1), goldCard.id, CardSide.BACK));
    gameFlowManager.addCommand(new DrawGoldDeckCardCommand(secondPlayerToken));
    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
  }
}