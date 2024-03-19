package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerItems;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * A playable card is a card that can be placed on the player board.
 */
public abstract class PlayableCard extends Card {
    /**
     * This constant map represents the corners of the front of the card.
     */
    private final Map<CornerPosition, Corner> frontCorners;
    /**
     * This constant map represents the corners of the back of the card.
     */
    private final Map<CornerPosition, Corner> backCorners;

    /**
     * This map represents the corners of the played side of the card.
     */
    private Map<CornerPosition, Corner> activeCorners;

    /**
     * This map represents the side card that is played
     */
    private CardSide playedSide;

    /**
     * @param playerResources resources owned by the player
     * @return True if there are enough resources to play the card, false otherwise
     */
    public boolean enoughResources(Map<CornerItems, Integer> playerResources) {
        return true;
    }

    public void playSide(CardSide playedSide) {
        this.playedSide = playedSide;
        this.activeCorners = new HashMap<>(playedSide == CardSide.FRONT ? frontCorners : backCorners);
    }

    /**
     * TODO: description
     * @param frontCorners corners of the front of the card.
     * @param backCorners corners of the back of the card.
     */
    PlayableCard(int id, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(id);
        this.frontCorners = frontCorners;
        this.backCorners = backCorners;
    }

    //GETTER
    public Map<CornerPosition, Corner> getActiveCorners() {
        return new HashMap<>(activeCorners);
    }

    public CardSide getPlayedSide() {
        return playedSide;
    }

    //SETTER
    public void setCorner(CornerPosition cornerPosition, Corner corner) {
        this.activeCorners.put(cornerPosition, corner);
    }
}
