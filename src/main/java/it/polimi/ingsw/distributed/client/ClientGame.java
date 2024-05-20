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
    public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleScoreTrackEvent'");
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayedCardEvent'");
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnGoldDeckCardEvent'");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnResourceDeckCardEvent'");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleResourceCardEvent'");
    }

    @Override
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCommonObjectiveEvent'");
    }

    @Override
    public void handleDirectMessageEvent(PlayerToken senderToken, PlayerToken receiverToken, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDirectMessageEvent'");
    }

    @Override
    public void handleGroupMessageEvent(PlayerToken senderToken, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGroupMessageEvent'");
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleTokenAssignmentEvent'");
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayedCardEvent'");
    }

    @Override
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayerElementsEvent'");
    }

    
    
}
