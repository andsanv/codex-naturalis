package it.polimi.ingsw.util;

import java.util.Objects;

/**
 * Utility class, where each object is a pair of three objects.
 *
 * @param <T1> first type
 * @param <T2> second type
 * @param <T3> third type
 *
 * @see Pair
 */
public final class Trio<T1, T2, T3> {
    /**
     * The first element of the pair.
     */
    public final T1 first;

    /**
     * The second element of the pair.
     */
    public final T2 second;

    /**
     * The third element of the pair.
     */
    public final T3 third;

    /**
     *
     * @param first first element of the pair
     * @param second second element of the pair
     * @param third third element of the pair
     */
    public Trio(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Allows to retrieve the index of the element given as parameter inside the Trio.
     *
     * @param object object of which to find the index
     * @return the index if the element is in the Trio, -1 otherwise
     */
    public Integer getIndexOf(Object object) {
        if (object == null) return -1;

        if (Objects.equals(object, first)) return 0;
        if (Objects.equals(object, second)) return 1;
        if (Objects.equals(object, third)) return 2;

        return -1;
    }

    /**
     * Allows to retrieve the first "empty" position, which is the position with the first null value
     *
     * @return index of the first null slot, -1 if all slots are full
     */
    public Integer getNullIndex() {
        if(first == null) return 0;
        if(second == null) return 1;
        if(third == null) return 2;

        return -1;
    }

    /**
     * Override of the Objects::equals method.
     * 
     * @param other the object to compare this to
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || other.getClass() != this.getClass()) return false;


        Trio<?, ?, ?> otherTrio = (Trio<?, ?, ?>) other;
        return Objects.equals(this.first, otherTrio.first) && Objects.equals(this.second, otherTrio.second) && Objects.equals(this.third, otherTrio.third);
    }

    /**
     * Override of the Objects::hash method.
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
