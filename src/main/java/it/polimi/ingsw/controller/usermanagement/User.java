package it.polimi.ingsw.controller.usermanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (userInfo == null)
            return null;

        synchronized (User.class) {
            return users.stream()
                    .filter(user -> user.equals(userInfo))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Checks if the given UserInfo is registered in the server.
     * 
     * @param userInfo the UserInfo of the user
     * 
     * @return true if the user exists
     */
    public static boolean exists(UserInfo userInfo) {
        synchronized (User.class) {
            return users.stream().anyMatch(u -> u.name.equals(userInfo.name) && u.id == userInfo.id);
        }
    }

    /**
     * Generates a random valid username of the given length.
     * If the length is smaller than 3, the username will have length 3.
     * 
     * @param length desired length for the username
     * 
     * @return the random generated username
     */
    public static String randomUsername(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        length = Math.min(length, 3);
        Random random = new Random();

        return random.ints(length, 0, chars.length())
                .mapToObj(i -> String.valueOf(chars.charAt(i)))
                .collect(Collectors.joining());
    }

    /**
     * Creates an UserInfo from the give user.
     * 
     * @param user the user
     * @return the UserInfo of the user
     */
    public synchronized UserInfo toUserInfo() {
        return this != null ? new UserInfo(this) : null;
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

    public boolean equals(User user) {
        return this.name.equals(user.name) && this.id == user.id;
    }

    public boolean equals(UserInfo userInfo) {
        return this.name.equals(userInfo.name) && this.id == userInfo.id;
    }
}
