package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.Map;

public abstract class PlayableCard extends Card {
    // These maps define both sides of the card
    private final Map<CornerPosition, Corner> frontCorners;
    private final Map<CornerPosition, Corner> backCorners;

    // This map represents the corners of the played side
    private Map<CornerPosition, Corner> activeCorners;

    PlayableCard(Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        this.frontCorners = frontCorners;
        this.backCorners = backCorners;
    }
}
