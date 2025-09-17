package controller.states;

import controller.GameFlowManager;
import controller.Server;
import controller.ServerPrinter;
import distributed.events.game.GameResultsEvent;
import model.card.ObjectiveCard;
import model.player.PlayerBoard;
import model.player.PlayerToken;
import util.Trio;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

        Server.INSTANCE.gameEnded(gameFlowManager.lobbyId);

        return true;
    }
}
