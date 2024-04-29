package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

public class DrawCardState extends GameState {
    public DrawCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    @Override
    public boolean drawResourceDeckCard(PlayerToken playerToken) {
        if(gameModelUpdater.drawResourceDeckCard(playerToken)) {
            gameFlowManager.increaseTurn();
            return true;
        }

        return false;
    }

    @Override
    public boolean drawGoldDeckCard(PlayerToken playerToken) {
        if(gameModelUpdater.drawGoldDeckCard(playerToken)) {
            gameFlowManager.increaseTurn();
            return true;
        }

        return false;
    }

    @Override
    public boolean drawVisibleResourceCard(PlayerToken playerToken, int choice) {
        if(gameModelUpdater.drawVisibleResourceCard(playerToken, choice)) {
            gameFlowManager.increaseTurn();
            return true;
        }

        return false;
    }

    @Override
    public boolean drawVisibleGoldCard(PlayerToken playerToken, int choice) {
        if(gameModelUpdater.drawVisibleGoldCard(playerToken, choice)) {
            gameFlowManager.increaseTurn();
            return true;
        }

        return false;
    }
}
