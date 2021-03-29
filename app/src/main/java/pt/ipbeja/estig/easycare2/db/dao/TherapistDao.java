package pt.ipbeja.estig.easycare2.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pt.ipbeja.estig.easycare2.db.entity.Therapist;

/**
 * The interface Therapist dao.
 */
//Dao is a data access abstraction layer. The Room library will generate code based on the methods and queries we define here.
@Dao
public interface TherapistDao {

    /**
     * Insert long.
     *
     * @param therapist the therapist
     * @return the long
     */
    @Insert
    long insert(Therapist therapist); // Returns long (the identifier assigned to the record)

    /**
     * Delete int.
     *
     * @param therapist the therapist
     * @return the int
     */
    @Delete
    int delete(Therapist therapist); // int is the number of records affected

    /**
     * Update int.
     *
     * @param therapist the therapist
     * @return the int
     */
    @Update
    int update(Therapist therapist); // int is the number of records affected

    /**
     * Gets all live data.
     *
     * @return the all live data
     */
// For select queries or, for example, a Delete that is not based solely on the record ID, we have the @Query annotation
    @Query("select * from therapist")
    LiveData<List<Therapist>> getAllLiveData();

    /**
     * Get therapist.
     *
     * @param therapistId the therapist id
     * @return the therapist
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from therapist where therapistId = :therapistId")
    Therapist get(long therapistId);


    /**
     * Gets seach name.
     *
     * @param searchName the search name
     * @return the seach name
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from therapist where fullName LIKE :searchName " )
    Therapist getSeachName(String searchName);


    @Query("select * from therapist")
    List<Therapist> getAll();
}
