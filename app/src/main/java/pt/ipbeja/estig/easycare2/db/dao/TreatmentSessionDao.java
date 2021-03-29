package pt.ipbeja.estig.easycare2.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pt.ipbeja.estig.easycare2.db.entity.TreatmentSession;


/**
 * The interface Treatment session dao.
 */
//Dao is a data access abstraction layer. The Room library will generate code based on the methods and queries we define here.
@Dao
public interface TreatmentSessionDao {


    /**
     * Insert long.
     *
     * @param treatmentSession the treatment session
     * @return the long
     */
    @Insert
    long insert(TreatmentSession treatmentSession); // Returns long (the identifier assigned to the record)


    /**
     * Delete int.
     *
     * @param treatmentSession the treatment session
     * @return the int
     */
    @Delete
    int delete(TreatmentSession treatmentSession); // int is the number of records affected


    /**
     * Update int.
     *
     * @param treatmentSession the treatment session
     * @return the int
     */
    @Update
    int update(TreatmentSession treatmentSession); // int is the number of records affected


    /**
     * Gets all live data.
     *
     * @return the all live data
     */
// For select queries or, for example, a Delete that is not based solely on the record ID, we have the @Query annotation
    @Query("select * from treatmentSession")
    LiveData<List<TreatmentSession>> getAllLiveData();


    /**
     * Get treatment session.
     *
     * @param idTreatment the id treatment
     * @return the treatment session
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from treatmentSession where idTreatment = :idTreatment")
    TreatmentSession get(long idTreatment);


    /**
     * Gets seach name.
     *
     * @param treatmentDate the treatment date
     * @return the seach name
     */
// For parameter-dependent queries, we define these parameters in the method and refer to those parameters by placing ':' behind the name
    @Query("select * from treatmentSession where treatmentDate = :treatmentDate " )
    TreatmentSession getSeachDateTrea(long treatmentDate);


}
