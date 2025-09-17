package view;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

import controller.usermanagement.UserInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * UserInfoManager is used for loading and saving an UserInfo instance to file.
 * At most one UserInfo can be saved at the same path at a time.
 */
public class UserInfoManager {
    /**
     * Attempts to read UserInfo from file.
     * The saved UserInfo is the last one used on the same machine in the same
     * location.
     * 
     * @return the user info as an optional if found, an empty optional if not found
     *         or if an error occurred
     */
    public static Optional<UserInfo> retrieveUserInfo() {
        // Check if a backup exists, open the stream and try to deserialize it
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("userInfo.ser"));
            UserInfo userInfo = (UserInfo) in.readObject();
            in.close();
            return Optional.of(userInfo);
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to save UserInfo to file, to use it for future logins.
     * 
     * @param userInfo the UserInfo of the user
     * 
     * @return true if successful, false otherwise
     */
    public static boolean saveUserInfo(UserInfo userInfo) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userInfo.ser"));
            out.writeObject(userInfo);
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}