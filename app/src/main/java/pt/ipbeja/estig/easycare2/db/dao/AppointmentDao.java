package pt.ipbeja.estig.easycare2.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pt.ipbeja.estig.easycare2.db.entity.Appointment;

/**
 * The interface Appointment dao.
 */
//Dao is a data access abstraction layer. The Room library will generate code based on the methods and queries we define here.
@Dao
public interface AppointmentDao {

    /**
     * Insert long.
     *
     * @param appointment the appointment
     * @return the long
     */
    @Insert
    long insert(Appointment appointment); // Returns long (the identifier assigned to the record)

    /**
     * Delete int.
     *
     * @param appointment the appointment
     * @return the int
     */
    @Delete
    int delete(Appointment appointment); // int is the number of records affected

    /**
     * Update int.
     *
     * @param appointment the appointment
     * @return the int
     */
    @Update
    int update(Appointment appointment); // int is the number of records affected

    /**
     * Gets all.
     *
     * @return the all
     */
// For select queries or, for example, a Delete that is not based solely on the record ID, we have the @Query annotation
    @Query("select * from appointment")
    LiveData<List<Appointment>> getAll();

    /**
     * Get appointment.
     *
     * @param appointmsntID the appointmsnt id
     * @return the appointment
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment where idAppointment = :appointmsntID")
    Appointment get(long appointmsntID);


    /**
     * Gets sort state.
     *
     * @return the sort state
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment order by state asc")
    LiveData<List<Appointment>> getSortState();

    /**
     * Gets sort start time.
     *
     * @return the sort start time
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment order by  starTime asc")
    LiveData<List<Appointment>> getSortStartTime();

    /**
     * Gets sort date consultation.
     *
     * @return the sort date consultation
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment order by consultationDate asc")
    LiveData<List<Appointment>> getSortDateConsultation();


    /**
     * Gets state.
     *
     * @param typeState the type state
     * @return the state
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment where state = :typeState")
    List<Appointment> getState(String typeState);


    /**
     * Gets appointment patient.
     *
     * @return the appointment patient
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment where idAppointment = :idAppointment")
    LiveData<List<Appointment>> getAppointmentPatient(long idAppointment);

    /**
     * Gets appointment therapist.
     *
     * @param therapistId the therapist id
     * @return the appointment therapist
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from appointment where therapistId = :therapistId")
    LiveData<List<Appointment>> getAppointmentTherapist(long therapistId);
}
