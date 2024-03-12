package it.polimi.ingsw.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
* This enum defines all the resource Items.
* */
public enum ResourceCards implements PlayableCard {
    CARD0(Resources.ANIMAL, Arrays.asList(Optional.of(CardItems.FUNGI), Optional.of(CardItems.EMPTY), Optional.empty(), Optional.of(CardItems.FUNGI)), false);
    // all the other cards ...

    final Resources resourceType;
    final List<Optional<CardItems>> frontCorners;
    final Boolean pointOnPlacement;

    ResourceCards(Resources resourceType, List<Optional<CardItems>> frontCorners, Boolean pointOnPlacement) {
        this.resourceType = resourceType;
        this.frontCorners = frontCorners;
        this.pointOnPlacement = pointOnPlacement;
    }
}