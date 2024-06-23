package it.polimi.ingsw.controller.states;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.distributed.events.game.GameResultsEvent;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

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
    List<Pair<PlayerToken, Integer>> results = gameModelUpdater.gameModel.scoreTrack.getScores().entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    gameFlowManager.notify(new GameResultsEvent(results));

    Lobby.deleteLobby(gameFlowManager.lobbyId);

    return true;
  }
}
