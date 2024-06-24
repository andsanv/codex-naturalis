package it.polimi.ingsw.util;

import java.io.Serializable;

/**
 * Utility class, where each object is a pair of two objects.
 *
 * @param <T1> first type
 * @param <T2> second type
 *
 * @see Trio
 */
public final class Pair<T1, T2> implements Serializable {
  /**
   * The first element of the pair.
   */
  public final T1 first;

  /**
   * The second element of the pair.
   */
  public final T2 second;

  /**
   * @param first first element of the pair
   * @param second second element of the pair
   */
  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }
}
