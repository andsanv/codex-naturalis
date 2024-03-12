package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.GoldCardPoints;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.corner.CornerItems;

import java.util.*;


public enum GoldCards implements PlayableCard {
    CARD0(Resources.ANIMAL, GoldCardPoints.ONE_PER_QUILL, new HashMap<Resources, Integer>() {{
        put(Resources.PLANT, 0);
        put(Resources.ANIMAL, 1);
        put(Resources.FUNGI, 2);
        put(Resources.INSECT, 0);
    }}, Arrays.asList(Optional.of(CornerItems.FUNGI), Optional.of(CornerItems.EMPTY), Optional.empty(), Optional.of(CornerItems.FUNGI)));
    // all the other cards ...

    final Resources resourceType;
    final GoldCardPoints pointType;
    final Map<Resources, Integer> resourcesNeeded;
    final List<Optional<CornerItems>> fontCorners;

    GoldCards(Resources resourceType, GoldCardPoints pointType, Map<Resources, Integer> resourcesNeeded, List<Optional<CornerItems>> frontCorners) {
        this.resourceType = resourceType;
        this.pointType = pointType;
        this.resourcesNeeded = resourcesNeeded;
        this.fontCorners = frontCorners;
    }
}