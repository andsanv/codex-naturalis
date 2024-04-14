package it.polimi.ingsw.model.corner;

import it.polimi.ingsw.model.common.Elements;

import java.util.Objects;
import java.util.Optional;

/**
 * TODO
 */
public class Corner {
    private final Optional<Elements> cornerElement;
    private CornerTypes cornerType;


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

    public Optional<Elements> getItem() {
        return cornerElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corner corner = (Corner) o;
        return Objects.equals(cornerElement, corner.cornerElement) && cornerType == corner.cornerType;
    }
}
