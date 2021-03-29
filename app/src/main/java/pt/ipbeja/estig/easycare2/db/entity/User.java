package pt.ipbeja.estig.easycare2.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * The type User.
 */
@Entity(tableName = "user")
public class User {

    // primary key tribute - self-generated id
    @PrimaryKey(autoGenerate = true)
    private long userId;
    private String email;
    private Boolean flagTypeUser;

    /**
     * Instantiates a new User.
     */
    @Ignore
    public User() {
    }

    /**
     * Instantiates a new User.
     *
     * @param email        the email
     * @param flagTypeUser the flag type user
     */
    @Ignore
    public User( String email,  Boolean flagTypeUser) {
        this.userId = 0;
        this.email = email;
        this.flagTypeUser = flagTypeUser;
    }

    /**
     * Instantiates a new User.
     *
     * @param userId       the user id
     * @param email        the email
     * @param flagTypeUser the flag type user
     */
    public User(long userId, String email, Boolean flagTypeUser) {
        this.userId = userId;
        this.email = email;
        this.flagTypeUser = flagTypeUser;

    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Gets flag type user.
     *
     * @return the flag type user
     */
    public Boolean getFlagTypeUser() {
        return flagTypeUser;
    }

    /**
     * Sets flag type user.
     *
     * @param flagTypeUser the flag type user
     */
    public void setFlagTypeUser(Boolean flagTypeUser) {
        this.flagTypeUser = flagTypeUser;
    }
}
