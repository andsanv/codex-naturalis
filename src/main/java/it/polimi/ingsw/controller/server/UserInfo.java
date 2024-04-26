package it.polimi.ingsw.controller.server;

/**
 * UserInfo contains the information about an user at the time it was generated.
 * The class can be safely shared since it is final and doesn't hold any
 * references.
 */
public final class UserInfo {
    public final String name;
    public final int id;

    public UserInfo(User user) {
        this.name = user.name;
        this.id = user.id;
    }

    @Override
    public String toString() {
        return name + "#" + id;
    }
}
