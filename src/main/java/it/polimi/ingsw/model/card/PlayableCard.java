package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

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
     * TODO: description
     * @param frontCorners corners of the front of the card.
     * @param backCorners corners of the back of the card.
     */
    PlayableCard(Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        this.frontCorners = frontCorners;
        this.backCorners = backCorners;
    }

    public Map<CornerPosition, Corner> getActiveCorners() {
        return activeCorners;
    }

}
