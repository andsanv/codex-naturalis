package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.gui.GUI;
import javafx.application.Platform;

import java.util.*;

public class SetupGamePhaseController extends Controller {
    private Map<UserInfo, PlayerToken> userInfoToToken = null;
    private UserInfo selfUserInfo = null;
    private GUI gui;

    // token selection

    // starter card selection

    // objective card selection
    List<Integer> availableObjectiveCards = null;

    public SetupGamePhaseController() {}

    public void initialize(GUI gui, List<UserInfo> players, UserInfo selfUserInfo) {
        this.gui = gui;
        this.selfUserInfo = selfUserInfo;

        userInfoToToken = new HashMap<>();
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken playerToken) {
        Platform.runLater(() -> {
            userInfoToToken.put(player, playerToken);
        });
    }

    @Override
    public void handleEndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached) {
        Platform.runLater(() -> {
            this.userInfoToToken = userInfoToToken;

            // TODO change pane
        });
    }

    @Override
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {

    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        Platform.runLater(() -> {

        });
    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        Platform.runLater(() -> {
            // TODO change pane
        });
    }

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int drawnCardId, int secondDrawnCardId) {
        Platform.runLater(() -> {
           if (playerToken != userInfoToToken.get(selfUserInfo)) return;

           availableObjectiveCards = new ArrayList<>(Arrays.asList(drawnCardId, secondDrawnCardId));

            // TODO update gui with the two cards
        });
    }

    @Override
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
        Platform.runLater(() -> {
            if (playerToken != userInfoToToken.get(selfUserInfo)) return;

            // TODO update gui with chosen card
        });
    }

    @Override
    public void handleEndedObjectiveCardPhaseEvent() {
        Platform.runLater(() -> {
            // TODO update gui with starting game transition
        });
    }

}
