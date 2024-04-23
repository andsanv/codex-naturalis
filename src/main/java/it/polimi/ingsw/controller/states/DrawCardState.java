package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawCardState extends GameState {
    public DrawCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    @Override
    public void drawResourceDeckCard(String playerId) {
        if(playerId.equals(playerIds.get(gameFlowManager.turn % playerIds.size())))
            gameModelUpdater.drawResourceDeckCard(IdToToken.get(playerId));

        endTurn();
    }

    @Override
    public void drawGoldDeckCard(String playerId) {
        if(playerId.equals(playerIds.get(gameFlowManager.turn % playerIds.size())))
            gameModelUpdater.drawGoldDeckCard(IdToToken.get(playerId));

        endTurn();
    }

    @Override
    public void drawVisibleResourceCard(String playerId, int choice) {
        if(playerId.equals(playerIds.get(gameFlowManager.turn % playerIds.size())))
            gameModelUpdater.drawVisibleResourceCard(IdToToken.get(playerId), choice);

        endTurn();
    }

    @Override
    public void drawVisibleGoldCard(String playerId, int choice) {
        if(playerId.equals(playerIds.get(gameFlowManager.turn % playerIds.size())))
            gameModelUpdater.drawVisibleGoldCard(IdToToken.get(playerId), choice);

        endTurn();
    }

    private void endTurn() {
        if(gameFlowManager.turn % playerIds.size() == playerIds.size() - 1) {
            if(gameFlowManager.isLastRound) {
                // stuff
                gameFlowManager.setState(gameFlowManager.postGameState);
                // gameFlowManager.currentState.handlePostGame();
                return;
            }
            else {
                if(gameModelUpdater.limitPointsReached() || gameModelUpdater.someDecksEmpty())
                    gameFlowManager.isLastRound = true;
                gameFlowManager.round += 1;
            }
        }

        gameFlowManager.turn += 1;
        gameFlowManager.setState(gameFlowManager.playCardState);
    }
}
