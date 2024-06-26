package it.polimi.ingsw.controller.usermanagement;

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

    /**
     * Constructor with name and id.
     * 
     * @param name the username
     * @param id   the id
     */
    public UserInfo(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Creates a UserInfo from the given string
     * 
     * @param userInfo the given string
     * @return the UserInfo
     * @throws IllegalArgumentException if the string is illegal
     */
    public static UserInfo fromString(String userInfo) throws IllegalArgumentException {
        String regex = "^[a-zA-Z0-9]+#[0-9]+$";

        if (!userInfo.matches(regex))
            throw new IllegalArgumentException(
                    "Username must be at least 3 characters long and can only contain alphanumeric characters.");

        String[] parts = userInfo.split("#");

        if (parts[0].length() < 3)
            throw new IllegalArgumentException(
                    "Username must be at least 3 characters long and can only contain alphanumeric characters.");

        return new UserInfo(parts[0], Integer.parseInt(parts[1]));
    }

    @Override
    public String toString() {
        return name + "#" + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        UserInfo other = (UserInfo) obj;
        return this.equals(other);
    }

    public boolean equals(UserInfo userInfo) {
        if (userInfo == null)
            return false;

        return this.name.equals(userInfo.name) && this.id == userInfo.id;
    }

    public boolean equals(User user) {
        if (user == null)
            return false;

        return this.name.equals(user.name) && this.id == user.id;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + id;
    }
}
