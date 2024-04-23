package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import it.polimi.ingsw.model.common.Resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A gold card is a playable card that requires resources to be placed on the
 * board.
 * All gold cards give points when played on the front side
 *
 * @see GoldCardPoints
 */
public class GoldCard extends PlayableCard {
    /**
     * This attribute represents the resource domain of the card.
     */
    private final Resources type;

    /**
     * This map represents the number of required resources of each type to play
     * this card.
     */
    private final Map<Resources, Integer> requiredResources;

    /**
     * TODO: add decription
     *
     * @param type              resource domain of the card.
     * @param points            points given when playing the front of the card.
     * @param requiredResources resources needed to play the card.
     * @param frontCorners      corners of the front of the card.
     * @param backCorners       corners of the back of the card.
     */
    public GoldCard(int id, Resources type, PointsType points, Map<Resources, Integer> requiredResources,
            Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(id, frontCorners, backCorners, points);
        this.type = type;
        this.requiredResources = requiredResources;
    }

    @Override
    public boolean enoughResources(Map<Elements, Integer> playerResources, CardSide side) {
        return side == CardSide.FRONT ? Arrays.stream(Resources.values())
                .allMatch(res -> playerResources.get(res) >= requiredResources.get(res)) : true;
    }

    @Override
    public Optional<Resources> getType() {
        return Optional.of(type);
    }

    public Map<Resources, Integer> getRequiredResources() {
        return new HashMap<>(requiredResources);
    }

}