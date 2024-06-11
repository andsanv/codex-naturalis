package it.polimi.ingsw.util;

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
}
