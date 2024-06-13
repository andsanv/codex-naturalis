package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** This interface must be defined by classes that handle game updates (GUIs, CLIs, etc). */
public interface GameEventHandler {
  public void handleScoreTrackEvent(PlayerToken senderToken, int score);

  public void handlePlayedCardEvent(
      PlayerToken playerToken,
      int playedCardId,
      CardSide playedCardSide,
      Coords playedCardCoordinates);

  public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex);

  public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex);

  public void handleDrawnVisibleResourceCardEvent(
      PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Optional<Integer> replacementCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex);

  public void handleDrawnVisibleGoldCardEvent(
          PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Optional<Integer> replacementCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex);

  public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId);

  public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide);

  public void handleEndedStarterCardPhaseEvent();

  public void handleDrawnObjectiveCardsEvent(
      PlayerToken playerToken, int drawnCardId, int secondDrawnCardId);

  public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId);

  public void handleEndedObjectiveCardPhaseEvent();

  public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId);

  public void handleDirectMessageEvent(
      PlayerToken senderToken, PlayerToken receiverToken, String message);

  public void handleGroupMessageEvent(PlayerToken senderToken, String message);

  public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken);

  public void handleEndedTokenPhaseEvent();

  public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId);

  public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources);

  public void handleGameError(String error);

  public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults);

  public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots, Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability);

  public void handleLimitPointsReachedEvent(PlayerToken playerToken, int score, int limitPoints);
}
