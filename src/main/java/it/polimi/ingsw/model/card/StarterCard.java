package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.player.PlayerBoard;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A starter card is the first card placed on each player's board.
 * At the start of the match, each player randomly draws one starter card.
 * @see PlayerBoard
 */
public class StarterCard extends PlayableCard {
    /**
     * This set holds the resources of the front of the card.
     */
    public final Set<Resources> centralResources;

    /**
     * TODO: add description
     * @param centralResources resources of the front of the card.
     * @param frontCorners corners of the front of the card.
     * @param backCorners corners of the back of the card.
     */
    StarterCard(int id, Set<Resources> centralResources, Map<CornerPosition, Corner> frontCorners, Map<CornerPosition, Corner> backCorners) {
        super(id, frontCorners, backCorners);
        this.centralResources = centralResources;
    }
}
