package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.GameModelUpdater;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.*;

public class SetupState extends GameState {
    public SetupState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    @Override
    public void setup() {
        Collections.shuffle(gameFlowManager.playersIds);

        // players choose their token
        playerIds.forEach(playerId -> {
            // TODO player chooses token
            PlayerToken chosen = null;
            IdToToken.put(playerId, chosen);
        });

        // players choose their starter card
        Map<PlayerToken, StarterCard> tokenToStarterCard = new HashMap<>();
        Map<PlayerToken, CardSide> tokenToCardSide = new HashMap<>();

        gameFlowManager.IdToToken.values().forEach(token -> {
            StarterCard starterCard = gameFlowManager.gameModelUpdater.drawStarterCard().get();
            CardSide chosenCardSide = null;     // TODO player chooses card side to play

            tokenToStarterCard.put(token, starterCard);
            tokenToCardSide.put(token, chosenCardSide);
        });

        // players choose their objective card
        Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard = new HashMap<>();

        for(PlayerToken playerToken : gameFlowManager.IdToToken.values()) {
            ObjectiveCard firstObjectiveCard = gameFlowManager.gameModelUpdater.drawObjectiveCard().get();
            ObjectiveCard secondObjectiveCard = gameFlowManager.gameModelUpdater.drawObjectiveCard().get();

            ObjectiveCard chosenObjectiveCard = null; // TODO player chooses objectiveCard
            tokenToObjectiveCard.put(playerToken, chosenObjectiveCard);
        }

        for(PlayerToken playerToken: gameFlowManager.IdToToken.values())
            gameFlowManager.gameModelUpdater.addPlayer(playerToken, tokenToStarterCard.get(playerToken), tokenToCardSide.get(playerToken), tokenToObjectiveCard.get(playerToken));

        // fill players' hands
        for(PlayerToken playerToken: gameFlowManager.IdToToken.values()) {
            gameFlowManager.gameModelUpdater.drawResourceDeckCard(playerToken);
            gameFlowManager.gameModelUpdater.drawResourceDeckCard(playerToken);
            gameFlowManager.gameModelUpdater.drawGoldDeckCard(playerToken);
        }

        // set common objectives
        List<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(gameFlowManager.gameModelUpdater.drawObjectiveCard().get());
        commonObjectives.add(gameFlowManager.gameModelUpdater.drawObjectiveCard().get());

        gameFlowManager.gameModelUpdater.setCommonObjectives(commonObjectives);

        // transition
        gameFlowManager.currentState = gameFlowManager.playCardState;
    }
}
