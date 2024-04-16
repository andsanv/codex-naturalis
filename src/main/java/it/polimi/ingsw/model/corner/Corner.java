package it.polimi.ingsw.model.corner;

import it.polimi.ingsw.model.common.Elements;

import java.util.Objects;
import java.util.Optional;

/**
 * This class represents a corner of a card.
 * Each card has four corners that can have different states.
 */
public class Corner {
    private final Optional<Elements> cornerElement;
    private CornerTypes cornerType;

    /**
     * @param cornerElement the element contained in corner
     * @param cornerType the corner's type (hidden, covered or visible)
     */
    public Corner(Elements cornerElement, CornerTypes cornerType) {
        this.cornerElement = Optional.ofNullable(cornerElement);
        this.cornerType = cornerType;
    }

    /**
     * @return True if a card can be placed on the corner, false otherwise
     */
    public boolean canPlaceCardAbove() {
        return cornerType == CornerTypes.VISIBLE;
    };

    /**
     * @return An optional containing the element in the corner. 
     */
    public Optional<Elements> getItem() {
        return cornerElement;
    }

    /**
     * @return the type of the corner
     */
    public CornerTypes getType() {
        return cornerType;
    }
    public void setType(CornerTypes cornerType) {
        this.cornerType = cornerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corner corner = (Corner) o;
        return Objects.equals(cornerElement, corner.cornerElement) && cornerType == corner.cornerType;
    }
}
