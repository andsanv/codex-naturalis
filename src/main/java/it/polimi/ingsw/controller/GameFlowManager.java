package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.states.DrawCardState;
import it.polimi.ingsw.controller.states.GameState;
import it.polimi.ingsw.controller.states.PlayCardState;
import it.polimi.ingsw.controller.states.SetupState;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerToken;
import jdk.javadoc.internal.tool.Start;
import sun.tools.jstat.Token;

import java.util.*;
import java.util.stream.Collectors;


//TODO: implement waiting room state in other controller (discuss)

public class GameFlowManager {
    private GameState setupState;
    private GameState playCardState;
    private GameState drawCardState;
    private GameState updateScoretrackState;

    private GameState currentState;

    private GameModelUpdater gameModelUpdater;

    private ArrayList<String> playersIds;
    private Map<String, PlayerToken> IdToToken;

    public GameFlowManager() {
        this.playCardState = new PlayCardState(this);
        this.drawCardState = new DrawCardState(this);
        this.setupState = new SetupState(this);
        this.updateScoretrackState = new PlayCardState.UpdateScoretrackState(this);

        this.currentState = this.setupState;
    }

    public void setup() {
        Collections.shuffle(playersIds);

        // players choose their token
        playersIds.forEach(playerId -> {
            //TODO PLAYER CHOOSES TOKEN
            PlayerToken chosen = null;
            IdToToken.put(playerId, chosen);
        });

        this.gameModelUpdater = new GameModelUpdater(new GameModel());

        // players choose their starter card
        Map<PlayerToken, StarterCard> tokenToStarterCard = new HashMap<>();

        IdToToken.values().forEach(token -> {
            tokenToStarterCard.put(token, gameModelUpdater.drawStarterCard().get());
        });

        // players choose their objective card
        Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard = new HashMap<>();
        for(PlayerToken playerToken : IdToToken.values()) {
            ObjectiveCard firstObjectiveCard = gameModelUpdater.drawObjectiveCard().get();
            ObjectiveCard secondObjectiveCard = gameModelUpdater.drawObjectiveCard().get();

            ObjectiveCard chosen = null; //player chooses
            tokenToObjectiveCard.put(playerToken, chosen);
        }

        for(PlayerToken playerToken: IdToToken.values())
            gameModelUpdater.addPlayer(playerToken, tokenToStarterCard.get(playerToken), tokenToObjectiveCard.get(playerToken));
    }

    public void dummy() {
        currentState.dummy();
    }


}




