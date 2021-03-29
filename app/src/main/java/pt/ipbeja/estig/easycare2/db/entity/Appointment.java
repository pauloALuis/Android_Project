package pt.ipbeja.estig.easycare2.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * The type Appointment.
 */
@Entity(tableName = "appointment")
public class Appointment {


    @PrimaryKey(autoGenerate = true)
    private long idAppointment;


    /*****id relaction ***/
    public long therapistId;
    public long idPatient;

    private long starTime;
    private String endTime;
    private String consultationDate;
    private String state;
    private String injuryDetails;



    /**
     * Instantiates a new Appointment.
     *
     * @param idAppointment    the id appointment
     * @param therapistId      the therapist id
     * @param idPatient        the id patient
     * @param starTime         the star time
     * @param endTime          the end time
     * @param consultationDate the date consultation
     * @param state            the state
     */
    public Appointment(long idAppointment, long therapistId, long idPatient, long starTime,
                       String endTime, String consultationDate, String injuryDetails, String state)
    {
        this.idAppointment = idAppointment;
        this.therapistId = therapistId;
        this.idPatient = idPatient;
        this.starTime = starTime;
        this.endTime = endTime;
        this.consultationDate = consultationDate;
        this.state = state;
        this.injuryDetails = injuryDetails;
    }

    /**
     * Instantiates a new Appointment.
     *
     * @param therapistId      the therapist id
     * @param idPatient        the id patient
     * @param starTime         the star time
     * @param endTime          the end time
     * @param consultationDate the date consultation
     * @param state            the state
     */
    @Ignore
    public Appointment(long therapistId, long idPatient, long starTime, String endTime,
                       String consultationDate, String injuryDetails, String state) {
        this.idAppointment = 0;
        this.therapistId = therapistId;
        this.idPatient = idPatient;
        this.starTime = starTime;
        this.endTime = endTime;
        this.consultationDate = consultationDate;
        this.state = state;
        this.injuryDetails = injuryDetails;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets id appointment.
     *
     * @return the id appointment
     */
    public long getIdAppointment() {
        return idAppointment;
    }

    /**
     * Sets id appointment.
     *
     * @param idAppointment the id appointment
     */
    public void setIdAppointment(long idAppointment) {
        this.idAppointment = idAppointment;
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
     * Gets star time.
     *
     * @return the star time
     */
    public long getStarTime() {
        return starTime;
    }

    /**
     * Sets star time.
     *
     * @param starTime the star time
     */
    public void setStarTime(long starTime) {
        this.starTime = starTime;
    }

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets date consultation.
     *
     * @return the date consultation
     */
    public String getConsultationDate() {
        return consultationDate;
    }

    /**
     * Sets date consultation.
     *
     * @param consultationDate the date consultation
     */
    public void setConsultationDate(String consultationDate) {
        this.consultationDate = consultationDate;
    }

    public String getInjuryDetails() {
        return injuryDetails;
    }

    public void setInjuryDetails(String injuryDetails) {
        this.injuryDetails = injuryDetails;
    }


    @Override
    public String toString() {
        return "Appointment{" +
                "idAppointment=" + idAppointment +
                ", therapistId=" + therapistId +
                ", idPatient=" + idPatient +
                ", starTime=" + starTime +
                ", endTime='" + endTime + '\'' +
                ", consultationDate='" + consultationDate + '\'' +
                ", state='" + state + '\'' +
                ", injuryDetails='" + injuryDetails + '\'' +
                '}';
    }
}
