package model;

/**
 * Author: Hieu Nguyen
 *
 * This class representing a model for a single user.
 * It contains the information for the user such as username
 * and password.
 */
public class User {
    private String mUsername;
    private String mPassword;

    /**
     * Construct a new user given their username and password.
     * @param username the user's new username.
     * @param password the user's new password.
     */
    public User(String username, String password) {
        this.mUsername = username;
        this.mPassword = password;
    }

    /**
     * Construct a new user with no information.
     */
    public User() {
        this("", "");
    }

    /**
     * Get the user's password.
     * @return the password of this user.
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * Set this user's password.
     * @param password the password for this user.
     */
    public void setPassword(String password) {
        this.mPassword = password;
    }

    /**
     * Get the user's username.
     * @return the username of this user.
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Set the user's username.
     * @param username the username for this user.
     */
    public void setUsername(String username) {
        this.mUsername = username;
    }

    /**
     * Create a return a string representation for this user.
     * @return a string representation for this user.
     */
    public String toString() {
        return mUsername;
    }
}
