package pt.ipbeja.estig.easycare2.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * The type Treatment session.
 */
@Entity(tableName = "treatmentSession")
public class TreatmentSession {


    @PrimaryKey(autoGenerate = true)
    private long idTreatment;


    /*****id relaction */
// @ForeignKey(entity = Therapist)
    public long therapistId;
    /**
     * The Id appointment.
     */
    public long idAppointment;
    /**
     * The Id patient.
     */
    public long idPatient;

    private long starTime;
    private long endTime;
    private long treatmentDate;


    /**
     * Instantiates a new Treatment session.
     *
     * @param idTreatment   the id treatment
     * @param therapistId   the therapist id
     * @param idAppointment the id appointment
     * @param idPatient     the id patient
     * @param starTime      the star time
     * @param endTime       the end time
     * @param treatmentDate the treatment date
     */
    public TreatmentSession(long idTreatment, long therapistId, long idAppointment, long idPatient,
                            long starTime, long endTime, long treatmentDate) {

        this.idTreatment = idTreatment;
        this.therapistId = therapistId;
        this.idAppointment = idAppointment;
        this.idPatient = idPatient;
        this.starTime = starTime;
        this.endTime = endTime;
        this.treatmentDate = treatmentDate;
    }

    /**
     * Instantiates a new Treatment session.
     *
     * @param therapistId   the therapist id
     * @param idAppointment the id appointment
     * @param idPatient     the id patient
     * @param starTime      the star time
     * @param endTime       the end time
     * @param treatmentDate the treatment date
     */
    @Ignore
    public TreatmentSession( long therapistId, long idAppointment, long idPatient,
                            long starTime, long endTime, long treatmentDate) {

        this.idTreatment = 0;
        this.therapistId = therapistId;
        this.idAppointment = idAppointment;
        this.idPatient = idPatient;
        this.starTime = starTime;
        this.endTime = endTime;
        this.treatmentDate = treatmentDate;
    }


    /**
     * Gets id treatment.
     *
     * @return the id treatment
     */
    public long getIdTreatment() {
        return idTreatment;
    }

    /**
     * Sets id treatment.
     *
     * @param idTreatment the id treatment
     */
    public void setIdTreatment(long idTreatment) {
        this.idTreatment = idTreatment;
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
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets treatment date.
     *
     * @return the treatment date
     */
    public long getTreatmentDate() {
        return treatmentDate;
    }

    /**
     * Sets treatment date.
     *
     * @param treatmentDate the treatment date
     */
    public void setTreatmentDate(long treatmentDate) {
        this.treatmentDate = treatmentDate;
    }
}
