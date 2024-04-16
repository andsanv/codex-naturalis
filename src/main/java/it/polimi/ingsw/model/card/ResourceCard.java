package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.common.Resources;

import java.util.Map;
import java.util.Optional;

/**
 * A resource card is a playable card that doesn't require any resources to be placed on the board.
 * Some resource cards can give points when played on the front side.
 * @see ResourceCardPoints
 */
public class ResourceCard extends PlayableCard {
    /**
     * This attribute represents the resource domain of the card.
     */
    private final Resources type;

    /**
     * @param type resource domain of the card
     * @param pointsType points given when playing the front of the card.
     * @param frontCorners corners of the front of the card.
     * @param backCorners corners of the back of the card.
     */
    public ResourceCard(int id, Resources type, PointsType pointsType, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(id, frontCorners, backCorners, pointsType);
        this.type = type;
    }

    @Override
    public Optional<Resources> getType() {
        return Optional.of(type);
    }
}