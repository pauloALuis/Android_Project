package pt.ipbeja.estig.easycare2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pt.ipbeja.estig.easycare2.db.entity.AppointmentWithName;

/**
 * The type Schedule therapist fragment.
 */
public class ScheduleTherapistFragment extends Fragment {

    private TextView textViewMonth;
    private FloatingActionButton btnAddCustomer;
    private CompactCalendarView compactCalendar;
    private List<AppointmentWithName> listAppointmentTherapist = new ArrayList<>();
    private static boolean checkUser;
    private JSONObject userInfoFromFile;
    private FirebaseFirestore referenceAppointment;
    private TextView textView;
    private String toalst ;
    private String toalst1;


    /********
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_shedule_therapist, container, false);
        this.toalst = getString(R.string.toast_date_appointment);
        this.toalst1 = getString(R.string.toast_patient);
        this.textView = inflate.findViewById(R.id.TextViewDitals);
        this.textView.setText("");
        textViewMonth = inflate.findViewById(R.id.textViewMonth);




        this.loadLogin(inflate);




        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        DateFormat dateFormat = new SimpleDateFormat("MMMM");
        textViewMonth.setText(dateFormat.format(new Date()));
        compactCalendar = (CompactCalendarView) inflate.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        // this.getEventCalendarAndOnClickDate();


        return inflate;
    }


    private void getEventCalendarAndOnClickDate() {



        //add event to calendara
        for (int i = 0; i < listAppointmentTherapist.size(); i++)
        {
           Event ev1 = new Event(Color.RED, listAppointmentTherapist.get(i).getStarTime(),toalst1 + ": " +
                    Useful.formatDate(listAppointmentTherapist.get(i).getStarTime()) +
                    toalst +": " + listAppointmentTherapist.get(i).getNamePatient());
            compactCalendar.addEvent(ev1);
        }

        this.onEventCalendara(toalst1, toalst);
    }


    /******
     * handles the click and shows event associated with the clicked day
     */
    private void onEventCalendara(String toalst1, String toalst) {

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Boolean b = false;
                for (AppointmentWithName withName: listAppointmentTherapist) {

                    String[] dateGet = Useful.formatDate(withName.getStarTime()).split(" ");
                    String[] dateCli = Useful.formatDate(dateClicked).split(" ");
                    if(dateCli[0].equals(dateGet[0]))
                    {
                        String string =   toalst1 + ": " + withName.getNamePatient()
                                + "h"
                                + "\n" +  toalst +": "+  Useful.formatDate(withName.getStarTime()) ;

                        textView.setText(string);
                        b = true;
                    }
                }
                if (!b){
                    String string = getString(R.string.toast_not_plane);
                    Toast.makeText(getContext(), string , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                //actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
                DateFormat dateFormat = new SimpleDateFormat("MMMM");
                textViewMonth.setText("");
                textViewMonth.setText(dateFormat.format(firstDayOfNewMonth.getMonth()));
               // textViewMonth.setText(firstDayOfNewMonth.getMonth()+"");

            }
        });
    }


    /*****
     * load user - reads the user's name in the file and calls the event list
     * @param inflate
     */
    private void loadLogin(View inflate) {
        this.userInfoFromFile = Useful.getUserInfoFromFile(getContext().getFilesDir());
        try {
            this.checkUser = (Boolean) userInfoFromFile.get("isTherapist");

            if (this.checkUser)
                this.loadListMarkedSession(this.checkUser, (int) userInfoFromFile.get("therapistId"), inflate);

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    /****
     *loads the list of orders or sessions already booked
     * @param checkUser
     * @param patientId
     * @param inflante
     */
    private void loadListMarkedSession(boolean checkUser, int patientId, View inflante) {
        this.referenceAppointment = FirebaseFirestore.getInstance();
        this.referenceAppointment.collection("AppointmentWithName")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Map<String, Object> AppointmentsData = document.getData();
                    long starTimeData = (long) AppointmentsData.get("starTime");
                    AppointmentWithName cuurretAppointment = new AppointmentWithName((long) AppointmentsData.get("idAppointment"),
                            (long) AppointmentsData.get("therapistId"), (long) AppointmentsData.get("idPatient"),
                            starTimeData, (String) AppointmentsData.get("endTime"), (String) AppointmentsData.get("consultationDate"),
                            (String) AppointmentsData.get("injuryDetails"), (String) AppointmentsData.get("state"),
                            (String) AppointmentsData.get("namePatient"), (String) AppointmentsData.get("nameTherapist"));

                    /****
                     * checks if the user is a therapist the patient
                     */
                    if (checkUser) {
                        /***
                         * lists the appointments associated with the therapist
                         */
                        if ((long) AppointmentsData.get("therapistId") == patientId) {
                            this.listAppointmentTherapist.add(cuurretAppointment);
                        }
                    }
                }
                this.getEventCalendarAndOnClickDate();
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }


    /****
     *change the title on the toolbar
     */
    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_shedule_therapist);
    }
}
