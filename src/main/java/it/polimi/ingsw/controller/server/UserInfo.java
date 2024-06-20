package it.polimi.ingsw.controller.server;

import java.io.Serializable;

/**
 * UserInfo contains the information about an user at the time it was generated.
 * The class can be safely shared since it is final and doesn't hold any
 * references.
 */
public final class UserInfo implements Serializable {
  public final String name;
  public final int id;

  /**
   * Main constructor that creates a UserInfo from a User.
   * 
   * @param user an User instance
   */
  public UserInfo(User user) {
    this.name = user.name;
    this.id = user.id;
  }

  /**
   * Copy-constructor.
   * 
   * @param userInfo the UserInfo to copy
   */
  public UserInfo(UserInfo userInfo) {
    this.name = userInfo.name;
    this.id = userInfo.id;
  }

  @Override
  public String toString() {
    return name + "#" + id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    UserInfo other = (UserInfo) obj;
    return this.name.equals(other.name) && this.id == other.id;
  }

  @Override
  public int hashCode() {
    return name.hashCode() + id;
  }
}
