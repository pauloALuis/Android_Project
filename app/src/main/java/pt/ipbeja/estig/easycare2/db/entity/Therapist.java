package pt.ipbeja.estig.easycare2.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * The type Therapist.
 */
@Entity(tableName = "therapist")
public class Therapist {

    // primary key tribute - self-generated id
    @PrimaryKey(autoGenerate = true)
    private long therapistId;
    private long phone;
    private String email;
    private String fullName;
    private long userId;
//    private String area;

    /**
     * Instantiates a new Therapist.
     *
     * @param therapistId the therapist id
     * @param phone       the phone
     * @param fullName    the full name
     * @param email       the email
     * @param userId      the user id
     */
    public Therapist(long therapistId, long phone, String fullName, String email, long userId) {
        this.therapistId = therapistId;
        this.phone = phone;
        this.fullName = fullName;
        this.email = email;
        this.userId = userId;
    }


    /**
     * Instantiates a new Therapist.
     *
     * @param fullName the full name
     * @param phone    the phone
     * @param email    the email
     * @param userId   the user id
     */
    @Ignore
    public Therapist( String fullName,long phone, String email, long userId) {
        this.therapistId = 0;
        this.phone = phone;
        this.fullName = fullName;
        this.email  = email;
        this.userId = userId;
    }

    /**
     * Instantiates a new Therapist.
     */
    @Ignore
    public Therapist( ) {

    }

    /**
     * Gets the user id
     *
     * @return User id
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the user id
     *
     * @param userId User id
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
     * Gets therapist id.
     *
     * @return the therapist id
     */
    public long getTherapistId() {
        return therapistId;
    }

    /**
     * Sets therapist id.
     *
     * @param therapistId the therapist id
     */
    public void setTherapistId(long therapistId) {
        this.therapistId = therapistId;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public long getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(long phone) {
        this.phone = phone;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets area.
     *
     * @return the area
     */
//    public String getArea() {
//        return area;
//    }

    /**
     * Sets area.
     *
     * @param area the area
     */
    public void setArea(String area) {
//        this.area = area;
    }
}
