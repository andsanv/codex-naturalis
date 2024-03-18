package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * A gold card is a playable card that requires resources to be placed on the board.
 * All gold cards give points when played on the front side
 * @see GoldCardPoints
 */
public class GoldCard extends PlayableCard {
    /**
     * This attribute represents the resource domain of the card.
     */
    public final Resources type;
    /**
     * This attribute represents the amount of points given to the player when he plays the front of this card.
     */
    public final GoldCardPoints points;

    /**
     * This map represents the number of required resources of each type to play this card.
     */
    public final Map<Resources, Integer> requiredResources;

    /**
     * TODO: add decription
     * @param type resource domain of the card.
     * @param points points given when playing the front of the card.
     * @param requiredResources resources needed to play the card.
     * @param frontCorners corners of the front of the card.
     * @param backCorners corners of the back of the card.
     */
    public GoldCard(int id, Resources type, GoldCardPoints points, Map<Resources, Integer> requiredResources, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(id, frontCorners, backCorners);
        this.type = type;
        this.points = points;
        this.requiredResources = requiredResources;
    }
}