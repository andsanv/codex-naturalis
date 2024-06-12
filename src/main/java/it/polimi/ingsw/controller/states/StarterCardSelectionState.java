package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.ChosenStarterCardSideEvent;
import it.polimi.ingsw.distributed.events.game.DrawnStarterCardEvent;
import it.polimi.ingsw.distributed.events.game.EndedStarterCardPhaseEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The state represents the game phase where players discover their starter card, and choose its side
 */
public class StarterCardSelectionState extends GameState {
  /**
   * List of tokens in the match
   */
  private final List<PlayerToken> playerTokens;

  /**
   * Decks used to draw setup cards.
   */
  private final Decks decks;

  /**
   * Time limit within which players need to decide their starter card side
   */
  private final long timeLimit;   // seconds

  /**
   * If true, the state machine assigns a random card side to remaining players
   */
  private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);

  /**
   * Map that tracks which starter card each token drew
   */
  private final Map<PlayerToken, StarterCard> TokenToStarterCard;

  /**
   * Map that tracks tokens and the chosen card side
   */
  private final Map<PlayerToken, CardSide> TokenToCardSide;

  public StarterCardSelectionState(
          GameFlowManager gameFlowManager, Decks decks, List<PlayerToken> playerTokens, long timeLimit) {
    super(gameFlowManager);

    this.timeLimit = timeLimit;

    this.decks = decks;
    this.playerTokens = playerTokens;
    this.TokenToStarterCard = new HashMap<>();
    this.TokenToCardSide = new HashMap<>();
  }

  /**
   * Waits for DrawStarterCardCommands and SelectStarterCardSideCommand by the players.
   * Players need to draw the card and choose the side within a certain time limit, otherwise a random card (with a random side) is assigned to them.
   * If time limit is reached, or if all players drew the card and chose the side, breaks from loop and returns the maps.
   * Throws a EndedStarterCardPhaseEvent, so that clients can update their UIs
   *
   * @return The two maps containing information on what card and what side each player chose
   */
  @Override
  public Pair<Map<PlayerToken, StarterCard>, Map<PlayerToken, CardSide>> handleStarterCardSelection() {
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
          }
        }
      else {
        Random random = new Random();

        List<CardSide> sides = new ArrayList<>(Arrays.asList(CardSide.FRONT, CardSide.BACK));

        playerTokens.stream()
            .filter(pt -> !TokenToStarterCard.containsKey(pt))
            .forEach(
                pt -> TokenToStarterCard.put(pt, decks.starterCardsDeck.draw(pt).orElseThrow()));
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

  /**
   * Handles the DrawStarterCardCommand
   * Throws a DrawnStarterCardCommand when finished
   *
   * @param playerToken player drawing the card
   * @return false if player has already drawn a card, true otherwise
   */
  @Override
  public boolean drawStarterCard(PlayerToken playerToken) {
    if (TokenToStarterCard.containsKey(playerToken)) return false;

    StarterCard starterCard = decks.starterCardsDeck.draw(playerToken).orElseThrow();

    TokenToStarterCard.put(playerToken, starterCard);
    decks.starterCardsDeck.notify(new DrawnStarterCardEvent(playerToken, starterCard.id));
    return true;
  }

  /**
   * Handles the SelectStarterCardSideCommand
   * Throws a ChosenStarterCardSideCommand when finished
   *
   * @param playerToken player drawing the card
   * @param cardSide side chosen
   * @return false if player has not drawn a card yet or if he already chose a side, true otherwise
   */
  @Override
  public boolean selectStarterCardSide(PlayerToken playerToken, CardSide cardSide) {
    if (!TokenToStarterCard.containsKey(playerToken) || TokenToCardSide.containsKey(playerToken))
      return false;

    TokenToCardSide.put(playerToken, cardSide);

    decks.starterCardsDeck.notify(new ChosenStarterCardSideEvent(playerToken, cardSide));
    return true;
  }
}
