package it.polimi.ingsw.distributed.client;

import java.util.Map;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;


public class ClientGame implements GameEventHandler {

    @Override
    public void handleScoreTrackEvent(UserInfo sender, int score) {
        System.out.println("\u001B[32mScore track event: " + sender + " - Score: " + score + "\u001B[0m");
    }

    @Override
    public void handlePlayedCardEvent(UserInfo player, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        System.out.println("\u001B[32mPlayed card event: " + player + " - Card ID: " + playedCardId + " - Card Side: " + playedCardSide + " - Coordinates: " + playedCardCoordinates + "\u001B[0m");
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(UserInfo player, int drawnCardId) {
        System.out.println("\u001B[32mDrawn gold deck card event: " + player + " - Card ID: " + drawnCardId + "\u001B[0m");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(UserInfo player, int drawnCardId) {
        System.out.println("\u001B[32mDrawn resource deck card event: " + player + " - Card ID: " + drawnCardId + "\u001B[0m");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(UserInfo player, int drawnCardPosition, int drawnCardId) {
        System.out.println("\u001B[32mDrawn visible resource card event: " + player + " - Card Position: " + drawnCardPosition + " - Card ID: " + drawnCardId + "\u001B[0m");
    }

    @Override
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        System.out.println("\u001B[32mCommon objective event: First Objective ID: " + firstCommonObjectiveId + " - Second Objective ID: " + secondCommonObjectiveId + "\u001B[0m");
    }

    @Override
    public void handleDirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {
        System.out.println("\u001B[32mDirect message event: Sender: " + sender + " - Receiver: " + receiver + " - Message: " + message + "\u001B[0m");
    }

    @Override
    public void handleGroupMessageEvent(UserInfo sender, String message) {
        System.out.println("\u001B[32mGroup message event: Sender: " + sender + " - Message: " + message + "\u001B[0m");
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        System.out.println("\u001B[32mToken assignment event: " + player + " - Assigned Token: " + assignedToken + "\u001B[0m");
    }

    @Override
    public void handlePlayedCardEvent(UserInfo player, int secretObjectiveCardId) {
        System.out.println("\u001B[32mPlayed card event: " + player + " - Secret Objective Card ID: " + secretObjectiveCardId + "\u001B[0m");
    }

    @Override
    public void handlePlayerElementsEvent(UserInfo player, Map<Elements, Integer> resources) {
        System.out.println("\u001B[32mPlayer elements event: " + player + " - Resources: " + resources + "\u001B[0m");
    }
}
