package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a card is played.
 */
public final class CommonObjectiveEvent extends GameEvent {
    private final int firstCommonObjectiveId;
    private final int secondCommonObjectiveId;

    public CommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        this.firstCommonObjectiveId = firstCommonObjectiveId;
        this.secondCommonObjectiveId = secondCommonObjectiveId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleCommonObjectiveEvent(firstCommonObjectiveId, secondCommonObjectiveId);

    }
}
