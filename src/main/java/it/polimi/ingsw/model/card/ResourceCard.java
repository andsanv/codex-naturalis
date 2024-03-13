package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.Map;

/*
*
*/
public class ResourceCard extends PlayableCard {
    private final Resources type;
    private final ResourceCardPoints points;

    ResourceCard(Resources type, ResourceCardPoints points, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(frontCorners, backCorners);
        this.type = type;
        this.points = points;
    }
}