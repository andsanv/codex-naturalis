package it.polimi.ingsw.controller.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;

public class UserTest {
    @Test
    void testEquals() {
        User user = new User("Test");
        UserInfo userInfo = new UserInfo(user);

        assertTrue(user.equals(userInfo));

        User user2 = new User("Test");

        assertNotEquals(user, user2);

        assertEquals(user, user);
    }

    @Test
    void testThrows() {
        assertThrows(IllegalArgumentException.class, () -> new User("A"));
        assertThrows(IllegalArgumentException.class, () -> new User("AAA#"));
        assertThrows(IllegalArgumentException.class, () -> new User(null));
    }

    @Test
    void testExists() {
        assertFalse(User.exists(new UserInfo("UtenteMaiCreato", 999)));
        User newUser = new User("NewUser");
        assertTrue(User.exists(new UserInfo(newUser)));
    }

    @Test
    void testGetUsers() {
        int oldSize = User.getUsers().size();

        User user1 = new User("Paolo");
        User user2 = new User("Francesca");

        Set<User> users = User.getUsers();

        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));

        assertEquals(oldSize + 2, users.size());
    }

    @Test
    void testRandomUsername() {
        String randomUsername = User.randomUsername(5);
        assertNotNull(randomUsername);
        assertTrue(randomUsername.matches("[a-zA-Z0-9]{3,}"));
    }

    @Test
    void testToUserInfo() {
        User user = new User("Pippo");
        UserInfo userInfo = new UserInfo("Pippo", 0);
        UserInfo userInfoConstructedFromUser = new UserInfo(user);

        assertEquals(user.toUserInfo(), userInfo);
        assertEquals(user.toUserInfo(), userInfoConstructedFromUser);

        assertNull(User.userInfoToUser(null));
    }

    @Test
    void testUserInfoToUser() {
        User user = new User("Pluto");
        UserInfo userInfo = new UserInfo(user);

        User foundUser = User.userInfoToUser(userInfo);

        assertEquals(user, foundUser);
    }
}
