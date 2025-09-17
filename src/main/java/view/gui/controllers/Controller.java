package view.gui.controllers;

import controller.usermanagement.LobbyInfo;
import controller.usermanagement.UserInfo;
import distributed.client.ConnectionHandler;
import model.SlimGameModel;
import model.card.CardSide;
import model.common.Elements;
import model.player.Coords;
import model.player.PlayerToken;
import util.Pair;
import util.Trio;
import view.UI;
import view.gui.GUI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Controller implements UI {
    protected GUI gui = null;
    protected AtomicReference<ConnectionHandler> connectionHandler = null;
    protected AtomicReference<UserInfo> selfUserInfo = null;

    public Controller() {
    }

    public void initialize() {}

    public void initialize(GUI gui) {
    }

    @Override
    public UserInfo getUserInfo() {
        return null;
    }

    @Override
    public void handleDisconnection() {

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
    public void handleDirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {

    }

    @Override
    public void handleGroupMessageEvent(UserInfo sender, String message) {

    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        return;
    }

    @Override
    public void handleEndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached) {

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
    public void handleGameResultsEvent(List<Trio<PlayerToken, Integer, Integer>> gameResults) {
        return;
    }

    @Override
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        return;
    }

    @Override
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {

    }

    @Override
    public void handlePlayerTurnEvent(PlayerToken currentPlayer) {

    }

    @Override
    public void handleLastRoundEvent() {

    }

    @Override
    public void handleLastConnectedPlayerEvent() {

    }

    @Override
    public void handleLastConnectedPlayerWonEvent() {

    }

    @Override
    public void handleLoginEvent(UserInfo userInfo, String error) {
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
    public void handleReconnetionToGame(SlimGameModel slimModel, Map<UserInfo, PlayerToken> userToToken) {
        return;
    }

    @Override
    public void handleJoinLobbyError(String error) {
        return;
    }

    @Override
    public void handleStartGameError(String message) {

    }

    @Override
    public void handleCreateLobbyError(String message) {

    }

    @Override
    public void handleLeaveLobbyError(String message) {

    }

    @Override
    public void handleGameStartedEvent(List<UserInfo> users) {

    }
}
