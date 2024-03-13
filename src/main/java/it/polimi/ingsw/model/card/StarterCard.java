package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.Map;
import java.util.Set;

public class StarterCard extends PlayableCard {
    private final Set<Resources> centralResources;

    StarterCard(Set<Resources> centralResources, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(frontCorners, backCorners);
        this.centralResources = centralResources;
    }
}
