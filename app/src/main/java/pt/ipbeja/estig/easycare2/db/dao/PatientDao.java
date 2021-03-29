package pt.ipbeja.estig.easycare2.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pt.ipbeja.estig.easycare2.db.entity.Patient;

/**
 * The interface Patient dao.
 */
//Dao is a data access abstraction layer. The Room library will generate code based on the methods and queries we define here.
@Dao
public interface PatientDao {

    /**
     * Insert long.
     *
     * @param patient the patient
     * @return the long
     */
    @Insert
    long insert(Patient patient); // Returns long (the identifier assigned to the record)

    /**
     * Delete int.
     *
     * @param patient the patient
     * @return the int
     */
    @Delete
    int delete(Patient patient); // int is the number of records affected

    /**
     * Update int.
     *
     * @param patient the patient
     * @return the int
     */
    @Update
    int update(Patient patient); // int is the number of records affected

    /**
     * Gets all live data.
     *
     * @return the all live data
     */
// For select queries or, for example, a Delete that is not based solely on the record ID, we have the @Query annotation
    @Query("select * from patient")
    LiveData<List<Patient>> getAllLiveData();

    /**
     * Get patient.
     *
     * @param idPatient the id patient
     * @return the patient
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from patient where idPatient = :idPatient")
    Patient get(long idPatient);


    /**
     * Gets seach name.
     *
     * @param searchName the search name
     * @return the seach name
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from patient where fullName LIKE :searchName " )
    Patient getSeachName(String searchName);


}
