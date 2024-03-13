package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.Map;

/*
 *
 */
public class GoldCard extends PlayableCard {
    private final Resources type;
    private final GoldCardPoints points;
    private final Map<Resources, Integer> requiredResources;

    GoldCard(Resources type, GoldCardPoints points, Map<Resources, Integer> requiredResources, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(frontCorners, backCorners);
        this.type = type;
        this.points = points;
        this.requiredResources = requiredResources;
    }
}