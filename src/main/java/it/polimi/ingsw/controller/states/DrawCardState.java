package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawCardState extends GameState {
    public DrawCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    @Override
    public void drawCard(String playerId, DrawChoices choice) {
        if(playerId.equals(playerIds.get(gameFlowManager.turn % playerIds.size())))
            switch(choice) {
                case RESOURCE_DECK:
                    gameModelUpdater.drawResourceDeckCard(IdToToken.get(playerId));
                    break;
                case RESOURCE_VISIBLE_FIRST:
                    gameModelUpdater.drawVisibleResourceCard(IdToToken.get(playerId), 0);
                    break;
                case RESOURCE_VISIBLE_SECOND:
                    gameModelUpdater.drawVisibleResourceCard(IdToToken.get(playerId), 1);
                    break;
                case GOLD_DECK:
                    gameModelUpdater.drawGoldDeckCard(IdToToken.get(playerId));
                    break;
                case GOLD_VISIBLE_FIRST:
                    gameModelUpdater.drawVisibleGoldCard(IdToToken.get(playerId), 0);
                    break;
                case GOLD_VISIBLE_SECOND:
                    gameModelUpdater.drawVisibleGoldCard(IdToToken.get(playerId), 1);
                    break;
            }

        if(gameFlowManager.turn % playerIds.size() == playerIds.size() - 1) {
            if(gameFlowManager.isLastRound) {
                // stuff
                gameFlowManager.setState(gameFlowManager.postGameState);
            }
            else {
                if(gameModelUpdater.limitPointsReached()  /* TODO if decks empty */)
                    gameFlowManager.isLastRound = true;
                gameFlowManager.round += 1;
            }
        }

        gameFlowManager.turn += 1;
        gameFlowManager.setState(gameFlowManager.playCardState);
    };
}
