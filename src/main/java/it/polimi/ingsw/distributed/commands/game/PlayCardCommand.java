package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;

public class PlayCardCommand extends GameCommand {
    public final String playerId;
    public final Coords coords;
    public final PlayableCard card;
    public final CardSide cardSide;

    public PlayCardCommand(String playerId, Coords coords, PlayableCard card, CardSide cardSide) {
        this.playerId = playerId;
        this.coords = coords;
        this.card = card;
        this.cardSide = cardSide;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.playCard(playerId, coords, card, cardSide);
    }
}
