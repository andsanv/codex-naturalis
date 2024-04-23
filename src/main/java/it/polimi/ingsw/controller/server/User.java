package it.polimi.ingsw.controller.server;

import java.util.HashMap;
import java.util.Map;

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

        synchronized(User.class) {
            id = nameToNextId.get(name);
            nameToNextId.put(name, (id != null ? id : 0) + 1);
        }

        this.id = id;
    }

    @Override
    public String toString() {
        return name + "#" + id;
    }

    @Override
    public boolean equals(Object obj) {
        User other = (User)obj;
        return this.name != other.name || this.id != other.id;
    }
}