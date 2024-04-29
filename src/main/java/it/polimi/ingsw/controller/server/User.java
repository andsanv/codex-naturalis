package it.polimi.ingsw.controller.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains an user's information (name and unique id).
 * The class is meant to be used on the server.
 * Synchronization on the class is implemented to ensure that all ids are
 * unique.
 */
public final class User {
    private static Map<String, Integer> nameToNextId = new HashMap<>();

    public final String name;
    public final int id;

    /**
     * Creates an user.
     * Users with the same name are given different ids.
     * 
     * @param name username
     */
    public User(String name) {
        this.name = name;

        Integer id;

        synchronized (User.class) {
            id = nameToNextId.getOrDefault(name, 0);
            nameToNextId.put(name, id + 1);
        }

        this.id = id;
    }

    @Override
    public String toString() {
        return name + "#" + id;
    }

    @Override
    public boolean equals(Object obj) {
        User other = (User) obj;
        return this.name == other.name && this.id == other.id;
    }

    // TODO add hashcode impl
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}