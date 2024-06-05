package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.EndedTokenPhaseEvent;
import it.polimi.ingsw.distributed.events.game.TokenAssignmentEvent;
import it.polimi.ingsw.model.player.PlayerToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenSelectionState extends GameState {
  private final List<User> users;

  private final long timeLimit; // in seconds
  private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);

  private final Map<String, PlayerToken> IdToToken;

  public TokenSelectionState(GameFlowManager gameFlowManager, List<User> users, long timeLimit) {
    super(gameFlowManager);
    this.users = users;

    this.timeLimit = timeLimit;
    this.timeLimitReached.set(true);

    this.IdToToken = new HashMap<>();
  }

  @Override
  public Map<String, PlayerToken> handleTokenSelection() {
    Timer timer = new Timer();

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
            if (IdToToken.keySet().size() == users.size()) {
              timer.cancel();
              break;
            }
          } else {
            // view.displayError("error");
          }
        }
      else {
        Random random = new Random();

        List<PlayerToken> availableTokens =
            new ArrayList<>(
                Arrays.asList(
                    PlayerToken.RED, PlayerToken.GREEN, PlayerToken.BLUE, PlayerToken.YELLOW));
        availableTokens.stream().filter(IdToToken::containsValue).forEach(availableTokens::remove);

        users.stream()
            .filter(u -> !IdToToken.containsKey(u.name))
            .forEach(
                u ->
                    IdToToken.put(
                        u.name, availableTokens.get(random.nextInt(availableTokens.size()))));

        break;
      }
    }

    IdToToken.forEach((id, playerToken) -> gameFlowManager.playerTokens.add(playerToken));
    gameFlowManager.setState(gameFlowManager.starterCardSelectionState);
    gameFlowManager.notify(new EndedTokenPhaseEvent());
    return new HashMap<>(IdToToken);
  }

  @Override
  public boolean selectToken(UserInfo player, PlayerToken playerToken) {
    synchronized (IdToToken) {
      if (IdToToken.containsKey(player.name) || IdToToken.containsValue(playerToken)) return false;

      IdToToken.put(player.name, playerToken);
      gameFlowManager.notify(new TokenAssignmentEvent(player, playerToken));
      return true;
    }
  }
}
