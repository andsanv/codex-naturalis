package it.polimi.ingsw.model;

import java.util.*;


public enum GoldCards implements Card {
    CARD0(Resources.ANIMAL, GoldCardPoints.ONE_PER_QUILL, new HashMap<Resources, Integer>() {{
        put(Resources.PLANT, 0);
        put(Resources.ANIMAL, 1);
        put(Resources.FUNGI, 2);
        put(Resources.INSECT, 0);
    }}, Arrays.asList(Optional.of(CardItems.FUNGI), Optional.of(CardItems.EMPTY), Optional.empty(), Optional.of(CardItems.FUNGI)));
    // all the other cards ...

    final Resources resourceType;
    final GoldCardPoints pointType;
    final Map<Resources, Integer> resourcesNeeded;
    final List<Optional<CardItems>> fontCorners;

    GoldCards(Resources resourceType, GoldCardPoints pointType, Map<Resources, Integer> resourcesNeeded, List<Optional<CardItems>> frontCorners) {
        this.resourceType = resourceType;
        this.pointType = pointType;
        this.resourcesNeeded = resourcesNeeded;
        this.fontCorners = frontCorners;
    }
}