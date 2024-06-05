package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.ChosenStarterCardSideEvent;
import it.polimi.ingsw.distributed.events.game.DrawnStarterCardEvent;
import it.polimi.ingsw.distributed.events.game.EndedStarterCardPhaseEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
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

public class StarterCardSelectionState extends GameState {
  private final long timeLimit; // in seconds
  private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);

  private final List<PlayerToken> playerTokens;
  private final Map<PlayerToken, StarterCard> TokenToStarterCard;
  private final Map<PlayerToken, CardSide> TokenToCardSide;

  public StarterCardSelectionState(
      GameFlowManager gameFlowManager, List<PlayerToken> playerTokens, long timeLimit) {
    super(gameFlowManager);

    this.timeLimit = timeLimit;

    this.playerTokens = playerTokens;
    this.TokenToStarterCard = new HashMap<>();
    this.TokenToCardSide = new HashMap<>();
  }

  @Override
  public Pair<Map<PlayerToken, StarterCard>, Map<PlayerToken, CardSide>>
      handleStarterCardSelection() {
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
            if (TokenToStarterCard.keySet().size() == playerTokens.size()
                && TokenToCardSide.keySet().size() == playerTokens.size()) {
              timer.cancel();
              break;
            }
          } else {
            // view.displayError("error");
          }
        }
      else {
        Random random = new Random();

        List<CardSide> sides = new ArrayList<>(Arrays.asList(CardSide.FRONT, CardSide.BACK));

        playerTokens.stream()
            .filter(pt -> !TokenToStarterCard.containsKey(pt))
            .forEach(
                pt -> TokenToStarterCard.put(pt, gameModelUpdater.drawStarterCard().orElse(null)));
        playerTokens.stream()
            .filter(pt -> !TokenToCardSide.containsKey(pt))
            .forEach(pt -> TokenToCardSide.put(pt, sides.get(random.nextInt(sides.size()))));

        break;
      }
    }

    gameFlowManager.setState(gameFlowManager.objectiveCardSelectionState);
    gameFlowManager.notify(new EndedStarterCardPhaseEvent());

    return new Pair<>(new HashMap<>(TokenToStarterCard), new HashMap<>(TokenToCardSide));
  }

  @Override
  public boolean drawStarterCard(PlayerToken playerToken) {
    if (TokenToStarterCard.containsKey(playerToken)) return false;

    StarterCard starterCard = gameModelUpdater.drawStarterCard().orElse(null);
    TokenToStarterCard.put(playerToken, starterCard);

    gameFlowManager.notify(new DrawnStarterCardEvent(playerToken, starterCard.getId()));
    return true;
  }

  @Override
  public boolean selectStarterCardSide(PlayerToken playerToken, CardSide cardSide) {
    if (!TokenToStarterCard.containsKey(playerToken) || TokenToCardSide.containsKey(playerToken))
      return false;

    TokenToCardSide.put(playerToken, cardSide);

    gameFlowManager.notify(new ChosenStarterCardSideEvent(playerToken, cardSide));
    return true;
  }
}
