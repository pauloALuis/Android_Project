package pt.ipbeja.estig.easycare2.db.dao;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pt.ipbeja.estig.easycare2.db.entity.Appointment;
import pt.ipbeja.estig.easycare2.db.entity.Patient;
import pt.ipbeja.estig.easycare2.db.entity.Therapist;
import pt.ipbeja.estig.easycare2.db.entity.User;

/**
 * The type Easy care database.
 */
@Database(entities = {Appointment.class, Patient.class, Therapist.class, User.class}, version = 2, exportSchema = false)
public abstract class EasyCareDatabase extends RoomDatabase {

    private static EasyCareDatabase INSTANCE = null;

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static synchronized  EasyCareDatabase getInstance(final Context context) {

        if(INSTANCE == null) {


            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), EasyCareDatabase.class, "easyCareDatabase2_db")
                    .allowMainThreadQueries()
                    .build();

        }
        return INSTANCE;
    }


    /**
     * Appointment dao appointment dao.
     *
     * @return the appointment dao
     */
    public abstract AppointmentDao appointmentDao();


    /**
     * Patient dao patient dao.
     *
     * @return the patient dao
     */
    public abstract PatientDao patientDao();


    /**
     * Therapist dao therapist dao.
     *
     * @return the therapist dao
     */
    public abstract TherapistDao therapistDao ();

}
