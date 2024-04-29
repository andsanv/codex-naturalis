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
    public boolean playCard(PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
        if(gameModelUpdater.playCard(playerToken, coords, card, cardSide)) {
            gameFlowManager.setState(gameFlowManager.drawCardState);
            return true;
        }

        return false;
    };
}
