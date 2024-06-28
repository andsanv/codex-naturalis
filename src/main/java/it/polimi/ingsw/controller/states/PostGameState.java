package it.polimi.ingsw.controller.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerPrinter;
import it.polimi.ingsw.controller.usermanagement.Lobby;
import it.polimi.ingsw.distributed.events.game.GameResultsEvent;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Trio;

/**
 * State that represents the "post-game" part of the game.
 * No actions on the boards are allowed.
 */
public class PostGameState extends GameState {
    public PostGameState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    /**
     * Manages post-game operations, sending to all clients the results of the game.
     * Deletes the lobby before returning.
     *
     * @return boolean that depends on whether the game ended successfully or not
     */
    @Override
    public boolean postGame() {
        List<ObjectiveCard> commonObjectives = gameModelUpdater.gameModel.commonObjectives;
        Map<PlayerToken, ObjectiveCard> secretObjectives = gameModelUpdater.gameModel.tokenToPlayer.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().secretObjective));
        Map<PlayerToken, PlayerBoard> playerBoards = gameModelUpdater.gameModel.tokenToPlayer.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().playerBoard));

        List<Trio<PlayerToken, Integer, Integer>> results = new ArrayList<>();

        for (Entry<PlayerToken, Integer> entry : gameModelUpdater.gameModel.scoreTrack.getScores().entrySet()) {
            int objectiveScore = 0;
            int completedObjectives = 0;

            PlayerToken token = entry.getKey();

            for (ObjectiveCard objectiveCard : commonObjectives) {
                int points = objectiveCard.computePoints(playerBoards.get(token));

                if (points != 0) {
                    objectiveScore += points;
                    completedObjectives++;
                }
            }

            ObjectiveCard secretObjective = secretObjectives.get(token);

            int points = secretObjective.computePoints(playerBoards.get(token));

            if (points != 0) {
                objectiveScore += points;
                completedObjectives++;
            }

            results.add(new Trio<PlayerToken, Integer, Integer>(token, entry.getValue() + objectiveScore,
                    completedObjectives));
        }

        Collections.sort(results, new Comparator<Trio<PlayerToken, Integer, Integer>>() {
            @Override
            public int compare(Trio<PlayerToken, Integer, Integer> t1, Trio<PlayerToken, Integer, Integer> t2) {
                int secondCompare = t2.second.compareTo(t1.second); // Descending order on the score
                if (secondCompare != 0) {
                    return secondCompare;
                } else {
                    return t2.third.compareTo(t1.third); // Descending order on the number of completed objectives
                }
            }
        });

        ServerPrinter.displayInfo("Game ended for lobby " + gameFlowManager.lobbyId);
        gameFlowManager.notify(new GameResultsEvent(results));
        Lobby.deleteLobby(gameFlowManager.lobbyId);
        Server.INSTANCE.broadcastLobbies();

        return true;
    }
}
