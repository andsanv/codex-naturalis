package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;

/**
 * Event to signal that a card has been played.
 */
public final class PlayedCardEvent extends GameEvent {
    private final UserInfo sender;
    private final int playedCardId;
    private final CardSide playedCardSide;
    private final Coords playedCardCoordinates;

    public PlayedCardEvent(UserInfo sender, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        this.sender = sender;
        this.playedCardId = playedCardId;
        this.playedCardSide = playedCardSide;
        this.playedCardCoordinates = playedCardCoordinates;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handlePlayedCardEvent(sender, playedCardId, playedCardSide, playedCardCoordinates);

    }
}
