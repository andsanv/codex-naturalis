package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.player.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: add javadoc
 */
public class GameModel {
    public final Map<PlayerToken, Player> tokenToPlayer;

    private Deck<ResourceCard> resourceCardsDeck;
    private List<ResourceCard> visibleResourceCards;

    private Deck<GoldCard> goldCardsDeck;
    private List<ResourceCard> visibleGoldCards;

    private Deck<ObjectiveCard> objectiveCardsDeck;
    private Deck<StarterCard> starterCardsDeck;

    private List<ObjectiveCard> commonObjectives;

    private ScoreTrack scoreTrack;

//    private final List<PlayerToken> playersOrder;

    GameModel() {
        // TODO
        tokenToPlayer = null;
//        playersOrder = null;
    }

    /**
     * This function must be called during the setup phase of the game
     *
     * @param playersCount number of players in the match
     * @return A list of starter cards of length players count
     */
    public List<StarterCard> getStarterCards(int playersCount) {
        // TODO this function must be called only once
        return null;
    }

    /**
     * This function must be called after players decide how to play their starter card
     *
     * @param tokenToStarterCard a map from player tokens to their starter card
     * @param cardsSidesPlayed a map with the played side of each starter card
     */
    public void setPlayersMap(Map<PlayerToken, StarterCard> tokenToStarterCard, Map<StarterCard, CardSide> cardsSidesPlayed) {
        // TODO this function must be called only once, after calling method getStarterCards()

        // TODO create here players and tokenToPlayer map

        // TODO randomly choose players' order (maybe not needed)
    }

    public void playCard(PlayerToken token, PlayableCard card, CardSide side, Coords coords) {
        // TODO play card and update scores if points increment is available
    }

    /**
     * TODO this class can become void, check inner todo
     *
     * @param playerToken the token of the player that draws the card
     * @return True if the card can be drawn, false otherwise
     */
    public boolean drawResourceCard(PlayerToken playerToken) {
        PlayerHand playerHand = tokenToPlayer.get(playerToken).getHand();

        // TODO check that the deck's not empty in the controller
        Optional<ResourceCard> card = resourceCardsDeck.draw();

        if (!card.isPresent())
            return false;

        playerHand.addCard(card.get());
            return true;
    }

    public boolean drawGoldCard(PlayerToken playerToken) {
        // TODO
        return false;
    }

    public boolean drawVisibleResourceCard(PlayerToken playerToken, int index) {
        // TODO draw visible resource card, add to hand and update visible cards by drawing from the deck (if it's not empty)
        return false;
    }

    public boolean drawVisibleGoldCard(PlayerToken playerToken, int index) {
        // TODO draw visible gold card, add to hand and update visible cards by drawing from the deck (if it's not empty)
        return false;
    }

    public void drawPlayerObjective(PlayerToken playerToken) {
        // TODO update player hand with drawn objective
    }

    public void drawCommonObjectives() {
        // TODO draw two common objective cards and add them to commonObjectives
    }

    // TODO maybe change method name
    public boolean are20PointsReached() {
        //TODO
        return scoreTrack.getScore().entrySet().stream().anyMatch(entry -> entry.getValue() >= 20);
    }

    public boolean isResourceCardsDeckEmpty() {
        return resourceCardsDeck.isEmpty();
    }

    public boolean isGoldCardsDeckEmpty() {
        return goldCardsDeck.isEmpty();
    }

    public int computeObjectivesPoints(PlayerToken playerToken) {
        //TODO return ObjectivesScoreCalculator.computeScore(...);
        return 0;
    }
}
