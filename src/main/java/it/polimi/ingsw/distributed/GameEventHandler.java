package it.polimi.ingsw.distributed;

import java.util.Map;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * This interface must be defined by classes that handle game updates (GUIs,
 * CLIs, etc).
 */
public interface GameEventHandler {
    public void handleScoreTrackEvent(UserInfo sender, int score);

    public void handlePlayedCardEvent(UserInfo player, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates);

    public void handleDrawnGoldDeckCardEvent(UserInfo player, int drawnCardId);

    public void handleDrawnResourceDeckCardEvent(UserInfo player, int drawnCardId);

    public void handleDrawnVisibleResourceCardEvent(UserInfo player, int drawnCardPosition, int drawnCardId);

    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId);

    public void handleDirectMessageEvent(UserInfo sender, UserInfo receiver, String message);

    public void handleGroupMessageEvent(UserInfo sender, String message);

    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken);

    public void handlePlayedCardEvent(UserInfo player, int secretObjectiveCardId);

    public void handlePlayerElementsEvent(UserInfo player, Map<Elements, Integer> resources);
}
