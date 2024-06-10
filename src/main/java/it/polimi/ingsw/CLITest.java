package it.polimi.ingsw;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.server.MainServerActions;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class CLITest implements UI {
  private static UserInfo userInfo = null;
  private static MainServerActions mainServerActions;
  private static MainViewActions mainViewActions;

  public CLITest(User user) {
    userInfo = new UserInfo(user);
  }

  @Override
  public UserInfo getUserInfo() {
    return userInfo;
  }

  @Override
  public void handleReceivedConnection() {
    System.out.println("Connection event received");
    try {
      mainServerActions.connectToMain(userInfo, mainViewActions);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void handleServerError(String error) {
    System.out.println("Server error: " + error);
  }

  @Override
  public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
    System.out.println("-------- New lobbies ----------");
    for (LobbyInfo lobby : lobbies) {
      System.out.println(lobby);
    }
    System.out.println("------------------------------");
  }

  @Override
  public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
    System.out.println("Score track event received");
    System.out.println("Sender Token: " + senderToken);
    System.out.println("Score: " + score);
  }

  @Override
  public void handlePlayedCardEvent(
      PlayerToken playerToken,
      int playedCardId,
      CardSide playedCardSide,
      Coords playedCardCoordinates) {
    System.out.println("Played card event received");
    System.out.println("Player Token: " + playerToken);
    System.out.println("Played Card ID: " + playedCardId);
    System.out.println("Played Card Side: " + playedCardSide);
    System.out.println("Played Card Coordinates: " + playedCardCoordinates);
  }

  @Override
  public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
    System.out.println("Drawn gold deck card event received");
    System.out.println("Player Token: " + playerToken);
    System.out.println("Drawn Card ID: " + drawnCardId);
  }

  @Override
  public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
    System.out.println("Drawn resource deck card event received");
    System.out.println("Player Token: " + playerToken);
    System.out.println("Drawn Card ID: " + drawnCardId);
  }

  @Override
  public void handleDrawnVisibleResourceCardEvent(
      PlayerToken playerToken, int drawnCardPosition, int drawnCardId) {
    System.out.println("Drawn visible resource card event received");
    System.out.println("Player Token: " + playerToken);
    System.out.println("Drawn Card Position: " + drawnCardPosition);
    System.out.println("Drawn Card ID: " + drawnCardId);
  }

  @Override
  public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
    System.out.println("Common objective event received");
    System.out.println("First Common Objective ID: " + firstCommonObjectiveId);
    System.out.println("Second Common Objective ID: " + secondCommonObjectiveId);
  }

  @Override
  public void handleDirectMessageEvent(
      PlayerToken senderToken, PlayerToken receiverToken, String message) {
    System.out.println("Direct message event received");
    System.out.println("Sender Token: " + senderToken);
    System.out.println("Receiver Token: " + receiverToken);
    System.out.println("Message: " + message);
  }

  @Override
  public void handleGroupMessageEvent(PlayerToken senderToken, String message) {
    System.out.println("Group message event received");
    System.out.println("Sender Token: " + senderToken);
    System.out.println("Message: " + message);
  }

  @Override
  public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
    System.out.println("Token assignment event received");
    System.out.println("Player: " + player);
    System.out.println("Assigned Token: " + assignedToken);
  }

  @Override
  public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
    System.out.println("Played card event received");
    System.out.println("Player Token: " + playerToken);
    System.out.println("Secret Objective Card ID: " + secretObjectiveCardId);
  }

  @Override
  public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
    System.out.println("Player elements event received");
    System.out.println("Player Token: " + playerToken);
    System.out.println("Resources: " + resources);
  }

  @Override
  public void handleGameError(String error) {
    System.out.println("Game error: " + error);
  }

  @Override
  public void handleUserInfo(UserInfo userInfo) {
    CLITest.userInfo = userInfo;
    System.out.println("UserInfo: " + userInfo);
  }

  @Override
  public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleDrawnStarterCardEvent'");
  }

  @Override
  public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleChosenStarterCardSideEvent'");
  }

  @Override
  public void handleEndedStarterCardPhaseEvent() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleEndedStarterCardPhaseEvent'");
  }

  @Override
  public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleDrawnObjectiveCardsEvent'");
  }

  @Override
  public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleChosenObjectiveCardEvent'");
  }

  @Override
  public void handleEndedObjectiveCardPhaseEvent() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleEndedObjectiveCardPhaseEvent'");
  }

  @Override
  public void handleEndedTokenPhaseEvent() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleEndedTokenPhaseEvent'");
  }

  @Override
  public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleGameResultsEvent'");
  }

  @Override
  public void handleCardsPlayabilityEvent(PlayerToken playerToken,
      Map<Integer, Pair<CardSide, List<Coords>>> cardsPlayability) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleCardsPlayabilityEvent'");
  }

  @Override
  public void connectionToGameResult(boolean connectedToGame) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'connectionToGameResult'");
  }
}
