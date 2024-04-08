package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.common.Resources;

import java.util.Map;

/**
 * A resource card is a playable card that doesn't require any resources to be placed on the board.
 * Some resource cards can give points when played on the front side.
 * @see ResourceCardPoints
 */
public class ResourceCard extends PlayableCard {
    /**
     * This attribute represents the resource domain of the card.
     */
    public final Resources type;
    /**
     * This attribute represents the amount of points given to the player when he plays the front of this card.
     */
    public final ResourceCardPoints points;

    /**
     * @param type resource domain of the card
     * @param points points given when playing the front of the card.
     * @param frontCorners corners of the front of the card.
     * @param backCorners corners of the back of the card.
     */
    public ResourceCard(int id, Resources type, ResourceCardPoints points, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(id, frontCorners, backCorners);
        this.type = type;
        this.points = points;
    }
}