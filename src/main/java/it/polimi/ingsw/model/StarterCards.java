package it.polimi.ingsw.model;

import java.util.Optional;

public enum StarterCards implements Card {
    CARD0(new Optional[]{}, new Resources[] {});

    final Optional<Resources>[] frontCornerResources;
    final Resources[] backCenterResources;

    StarterCards(Optional<Resources>[] frontCornerResources, Resources[] backCenterResources) {
        this.frontCornerResources = frontCornerResources;
        this.backCenterResources = backCenterResources;
    }
}
