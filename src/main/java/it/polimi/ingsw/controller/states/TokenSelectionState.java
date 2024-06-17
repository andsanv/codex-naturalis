package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.EndedTokenPhaseEvent;
import it.polimi.ingsw.distributed.events.game.TokenAssignmentEvent;
import it.polimi.ingsw.model.player.PlayerToken;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The state represents the game phase where players decide their game tokens
 */
public class TokenSelectionState extends GameState {

  /**
   * Map that tracks connection between a player and his token.
   */
  private final Map<UserInfo, PlayerToken> userInfoToToken;

  /**
   * time limit within which the players need to choose their token.
   */
  private final long timeLimit; // in seconds

  /**
   * If true, the state machine assigns random tokens to players who have not chosen their token yet
   */
  private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);


  public TokenSelectionState(GameFlowManager gameFlowManager, List<UserInfo> users, long timeLimit) {
    super(gameFlowManager);
    this.users = users;

    this.timeLimit = timeLimit;

    this.userInfoToToken = new HashMap<>();
  }

  /**
   * Waits for tokenAssignmentCommands by the players.
   * Players need to choose the token within a certain time limit, otherwise a random token is chosen for them.
   * If time limit is reached, or if all players chose the token, breaks from loop and returns the map.
   * Throws a EndedTokenPhaseEvent, so that clients can update their UIs
   *
   * @return The final player to chosen token map
   */
  @Override
  public Map<UserInfo, PlayerToken> handleTokenSelection(List<PlayerToken> playerTokens) {
    Timer timer = new Timer();
    
    System.out.println("limit time is: " + timeLimit);

    Queue<GameCommand> commands = gameFlowManager.commands;

    TimerTask timeElapsedTask =
        new TimerTask() {
          @Override
          public void run() {
            synchronized (timeLimitReached) {
              timeLimitReached.set(true);
            }
          }
        };
    timer.schedule(timeElapsedTask, timeLimit * 1000);

    while (true) {
      if (!timeLimitReached.get())
        synchronized (commands) {
          if (!commands.isEmpty() && commands.poll().execute(gameFlowManager)) {
            if (userInfoToToken.keySet().size() == users.size()) {
              timer.cancel();
              break;
            }
          }
        }
      else {
        Random random = new Random();

        List<PlayerToken> availableTokens = Arrays.asList(PlayerToken.RED, PlayerToken.GREEN, PlayerToken.BLUE, PlayerToken.YELLOW)
          .stream()
          .filter(x -> !userInfoToToken.containsValue(x))
          .collect(Collectors.toList());

        users.stream()
            .filter(u -> !userInfoToToken.containsKey(u))
            .forEach(
                u -> userInfoToToken.put(u, availableTokens.get(random.nextInt(availableTokens.size())))
            );

        break;
      }
    }

    userInfoToToken.forEach((id, playerToken) -> playerTokens.add(playerToken));
    gameFlowManager.setState(gameFlowManager.starterCardSelectionState);
    gameFlowManager.notify(new EndedTokenPhaseEvent());
    return new HashMap<>(userInfoToToken);
  }

  /**
   * Checks whether a requested token is available.
   * Returns false if a player already chose a token, or if the token chosen is not available
   * Sends a tokenAssignmentEvent if operation was successful
   *
   * @param player player choosing the token
   * @param playerToken token chosen by the player
   * @return false if a player already chose a token, or if the token chosen is not available, true otherwise
   */
  @Override
  public boolean selectToken(UserInfo player, PlayerToken playerToken) {
    synchronized (userInfoToToken) {
      if (userInfoToToken.containsKey(player) || userInfoToToken.containsValue(playerToken)) return false;
      
      userInfoToToken.put(player, playerToken);
      gameFlowManager.notify(new TokenAssignmentEvent(player, playerToken));
      System.out.println(player.name + " has selected token " + (playerToken == PlayerToken.RED ? "RED" : "BLUE"));
      return true;
    }
  }
}
