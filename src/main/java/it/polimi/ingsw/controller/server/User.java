package it.polimi.ingsw.controller.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains an user's information (name and unique id). The class is meant to be
 * used on the server.
 * Synchronization on the class is implemented to ensure that all ids are
 * unique.
 */
public final class User {
    /**
     * A map from a name to the number of its occurences.
     */
    private static Map<String, Integer> nameToNextId = new HashMap<>();

    /**
     * All the registered users.
     */
    private static final Set<User> users = new HashSet<>();

    public final String name;
    public final int id;

    /**
     * Creates an user. Users with the same name are given different ids.
     * The name must be at least 3 characters long and can only contain alphanumeric
     * characters.
     * 
     * @param name the username
     * @throws IllegalArgumentException
     */
    public User(String name) {
        if (name == null || name.length() < 3 || !name.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException(
                    "Username must be at least 3 characters long and can only contain alphanumeric characters.");
        }

        this.name = name;

        Integer id;

        synchronized (User.class) {
            id = nameToNextId.getOrDefault(name, 0);
            nameToNextId.put(name, id + 1);
        }

        this.id = id;

        synchronized (User.class) {
            users.add(this);
        }
    }

    /**
     * Returns a set containing the references to all registered users.
     * 
     * @return the set of users
     */
    public static Set<User> getUsers() {
        synchronized (User.class) {
            return new HashSet<>(users);
        }
    }

    /**
     * Finds the User matching the given UserInfo.
     * If no user is found this method returns null.
     * 
     * @param userInfo the player UserInfo
     * @return the User if found, null otherwise
     * 
     * @see User
     * @see UserInfo
     */
    public static User userInfoToUser(UserInfo userInfo) {
        synchronized (User.class) {
            return users.stream()
                    .filter(user -> user.name.equals(userInfo.name) && user.id == userInfo.id)
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public String toString() {
        return name + "#" + id;
    }

    @Override
    public boolean equals(Object obj) {
        User other = (User) obj;
        return this.name.equals(other.name) && this.id == other.id;
    }
}
