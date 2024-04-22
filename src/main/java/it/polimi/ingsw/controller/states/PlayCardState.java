package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public class PlayCardState extends GameState {
    public PlayCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    @Override
    public void playCard(String playerId, Coords coords, PlayableCard card, CardSide cardSide) {
        if(playerId.equals(playerIds.get(gameFlowManager.turn % playerIds.size()))) {
            gameModelUpdater.playCard(IdToToken.get(playerId), coords, card, cardSide);
        }

        gameFlowManager.setState(gameFlowManager.drawCardState);
    };
}
