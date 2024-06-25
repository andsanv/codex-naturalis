package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.gui.GUI;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Controller implements UI {
    public final GUI gui;

    public Controller(GUI gui) {
        this.gui = gui;
    }

    @Override
    public UserInfo getUserInfo() {
        return null;
    }

    @Override
    public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
        return;
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        return;
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        return;
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        return;
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        return;
    }

    @Override
    public void handleDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        return;
    }

    @Override
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        return;
    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        return;
    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        return;
    }

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int drawnCardId, int secondDrawnCardId) {
        return;
    }

    @Override
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
        return;
    }

    @Override
    public void handleEndedObjectiveCardPhaseEvent() {
        return;
    }

    @Override
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        return;
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        return;
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
        return;
    }

    @Override
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        return;
    }

    @Override
    public void handleGameError(String error) {
        return;
    }

    @Override
    public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
        return;
    }

    @Override
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        return;
    }

    @Override
    public void handleLimitPointsReachedEvent(PlayerToken playerToken, int score, int limitPoints) {
        return;
    }

    @Override
    public void handleLoginEvent(UserInfo userInfo, String error) {
        return;
    }

    @Override
    public void handleServerError(String error) {
        return;
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        return;
    }

    @Override
    public void handleReceivedConnection() {
        return;
    }

    @Override
    public void handleRefusedReconnection() {
        return;
    }

    @Override
    public void handleReconnetionToGame(SlimGameModel slimModel, Map<UserInfo, PlayerToken> userToToken) {
        return;
    }
}
