package pt.ipbeja.estig.easycare2.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * The type Patient.
 */
@Entity(tableName = "patient")
public class Patient {


    // primary key tribute - self-generated id
    @PrimaryKey(autoGenerate = true)
    private long idPatient;
    private long phone;
    private String fullName;
    private String emailPatient;
    private String codPostal;
    private String address;
    private String city;
    private long userId;

    /**
     * Instantiates a new Patient.
     */
    @Ignore
    public Patient() {
    }

    /**
     * Sets email patient.
     *
     * @param emailPatient the email patient
     */
    public void setEmailPatient(String emailPatient) {
        this.emailPatient = emailPatient;
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
     * Instantiates a new Patient.
     *
     * @param idPatient    the id patient
     * @param phone        the phone
     * @param fullName     the full name
     * @param emailPatient the email
     * @param codPostal    the cod postal
     * @param address      the address
     * @param city         the city
     * @param userId       the user id
     */
    public Patient(long idPatient, long phone, String fullName, String emailPatient,
                   String codPostal, String address, String city, long userId) {
        this.idPatient = idPatient;
        this.phone = phone;
        this.fullName = fullName;
        this.emailPatient = emailPatient;
        this.codPostal = codPostal;
        this.address = address;
        this.city = city;
        this.userId = userId;
    }

    /**
     * Instantiates a new Patient.
     *
     * @param fullName  the full name
     * @param email     the email
     * @param phone     the phone
     * @param codPostal the cod postal
     * @param address   the address
     * @param city      the city
     * @param userId    the user id
     */
    @Ignore
    public Patient(String fullName, String email, long phone, String codPostal, String address,
                   String city, long userId) {
        this.idPatient = 0;
        this.phone = phone;
        this.fullName = fullName;
        this.emailPatient = email;
        this.codPostal = codPostal;
        this.address = address;
        this.city = city;
        this.userId = userId;

    }


    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets id patient.
     *
     * @return the id patient
     */
    public long getIdPatient() {
        return idPatient;
    }

    /**
     * Sets id patient.
     *
     * @param idPatient the id patient
     */
    public void setIdPatient(long idPatient) {
        this.idPatient = idPatient;
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
     * Gets email.
     *
     * @return the email
     */
    public String getEmailPatient() {
        return emailPatient;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.emailPatient = email;
    }

    /**
     * Gets cod postal.
     *
     * @return the cod postal
     */
    public String getCodPostal() {
        return codPostal;
    }

    /**
     * Sets cod postal.
     *
     * @param codPostal the cod postal
     */
    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}