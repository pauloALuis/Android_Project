package pt.ipbeja.estig.easycare2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ipbeja.estig.easycare2.db.dao.EasyCareDatabase;
import pt.ipbeja.estig.easycare2.db.entity.Appointment;
import pt.ipbeja.estig.easycare2.db.entity.AppointmentWithName;
import pt.ipbeja.estig.easycare2.db.entity.Therapist;

/**
 * The type Make appointment fragment.
 */
public class MakeAppointmentFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private Button buttonChooseHour;
    private Button buttonChooseDate;
    private TextView textViewChooseHour;
    private TextView textViewChooseDate;
    private EditText editTextDetailsIng;
    private Spinner spinnerTherapist;
    private TimePickerDialog timePickerDialog;
    private FirebaseDatabase fireDatabase;
    public DatabaseReference userReference;
    private JSONObject userInfoFromFile;
    private FirebaseFirestore db;
    private FloatingActionButton buttonSaveSession;
    private int currentHour;
    private int currentMinute;
    private Calendar calendar;
    private List<Therapist> listTherapist = new ArrayList<Therapist>();
    private List<String> listSpinner = new ArrayList<String>();
    private int idPatient;
    private int maxIdAppointments;
    private String nameUser;

    /********
     * on Create view layout Make appointment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     **/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_make_appointment, container, false);
        this.db = FirebaseFirestore.getInstance();

        this.buttonChooseDate = inflate.findViewById(R.id.buttonChooseDate);
        this.buttonChooseHour = inflate.findViewById(R.id.buttonChooseHours);
        this.buttonChooseDate = inflate.findViewById(R.id.buttonChooseDate);
        this.buttonChooseHour = inflate.findViewById(R.id.buttonChooseHours);
        this.textViewChooseHour = inflate.findViewById(R.id.textViewHours);
        this.textViewChooseDate = inflate.findViewById(R.id.textViewDate);
        this.editTextDetailsIng = inflate.findViewById(R.id.editTextInjuryDetails);
        this.spinnerTherapist = inflate.findViewById(R.id.spinnerTherapist);
        this.buttonSaveSession = inflate.findViewById(R.id.floatingButtonSaveSession);
        this.setButtonChooseHour(buttonChooseHour);
        this.buttonShowDatePicker(buttonChooseDate);
        this.buttonConfirmSession(buttonSaveSession);
        this.createListTherapist();
        this.verifyUserID();
        this.fetchValueIds();
        return inflate;
    }

    /*****
     * check the user id current
     */
    private void verifyUserID() {
        this.fireDatabase = FirebaseDatabase.getInstance();
        this.userReference = fireDatabase.getReference("users");
        this.userInfoFromFile = Useful.getUserInfoFromFile(getContext().getFilesDir());

        try {
            this.idPatient = (int) userInfoFromFile.get("patientId");
            this.nameUser = (String) userInfoFromFile.get("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("###is User: ", String.valueOf(this.idPatient));
    }

    /**
     * Gets text view choose hour.
     *
     * @return the text view choose hour
     */
    public String getTextViewChooseHour() {
        return textViewChooseHour.getText().toString();
    }

    /**
     * Sets text view choose hour.
     *
     * @param textViewChooseHour the text view choose hour
     */
    public void setTextViewChooseHour(String textViewChooseHour) {
        this.textViewChooseHour.setText(textViewChooseHour);
    }

    /**
     * Gets text view choose date.
     *
     * @return the text view choose date
     */
    public String getTextViewChooseDate() {
        return textViewChooseDate.getText().toString();
    }

    /**
     * Sets text view choose date.
     *
     * @param textViewChooseDate the text view choose date
     */
    public void setTextViewChooseDate(String textViewChooseDate) {
        this.textViewChooseDate.setText(textViewChooseDate);
    }

    /**
     * Gets edit text details ing.
     *
     * @return the edit text details ing
     */
    public String getEditTextDetailsIng() {
        return editTextDetailsIng.getText().toString();
    }

    /**
     * Sets edit text details ing.
     *
     * @param editTextDetailsIng the edit text details ing
     */
    public void setEditTextDetailsIng(String editTextDetailsIng) {
        this.editTextDetailsIng.setText(editTextDetailsIng);
    }

    /**
     * Gets spinner therapist.
     *
     * @return the spinner therapist
     */
    public String getSpinnerTherapist() {
        return spinnerTherapist.getSelectedItem().toString();
    }

    /**
     * Gets choose id therapist.
     *
     * @return the id therapist
     */
    public long getIdTherapist() {

        String myString = getSpinnerTherapist();
        if (!myString.isEmpty()) {
            final String[] tokens = myString.split("\\-");
            return Useful.convertStringOfInt(tokens[0]);
        }
        return -1;
    }

    /**
     * Gets choose id therapist.
     *
     * @return the id therapist
     */
    public String getNameTherapist() {

        String myString = getSpinnerTherapist();
        if (!myString.isEmpty()) {
            final String[] tokens = myString.split("\\-");
            return tokens[1];
        }
        return "";
    }

    /**
     * Gets choose id therapist.
     * @param position the position
     * @return the id therapist
     */
    public int splitYear(int position) {
        String textDate = getTextViewChooseDate();
        if (!textDate.isEmpty()) {
            final String[] tokens = textDate.split("\\-");
            return Useful.convertStringOfInt(tokens[position]);
        }
        return -1;
    }

    /**
     * Gets choose id therapist.
     *
     * @param position the position
     * @return the id therapist
     */
    public int splitHours(int position) {
        String textDate = getTextViewChooseHour();
        if (!textDate.isEmpty()) {
            final String[] tokens = textDate.split("\\:");
            if (position == 1) {
                return Useful.convertStringOfInt(tokens[position].substring(0, 2));
            }
            return Useful.convertStringOfInt(tokens[position]);
        }
        return -1;
    }

    /****
     * confirm the data and asks to save
     * @param buttonSaveSession
     */
    private void buttonConfirmSession(FloatingActionButton buttonSaveSession) {
        buttonSaveSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*****
                 * validate field
                 */
                if (validateFieldIsFilledTextSpinner(getString(R.string.toast_enter_name))
                        || Useful.validateFieldIsFilledTextView(buttonChooseHour, textViewChooseHour
                        , getString(R.string.toast_enter_hour))
                        || Useful.validateFieldIsFilledTextView(buttonChooseDate, textViewChooseDate
                        , getString(R.string.toast_enter_date))) {
                    return;
                } else {

                    int hours = splitHours(0);
                    int min = splitHours(1);
                    long dateStart = Useful.createLongDate(splitYear(2), splitYear(1)
                            , splitYear(0), hours, min);

                    hours += 1;
                    String hoursEnd = hours + ":" + min + "h";

                    AppointmentWithName appointment = new AppointmentWithName((long) getIdTherapist(),
                            (long) idPatient, dateStart, hoursEnd, "1h",
                            getEditTextDetailsIng(), "PEDIDO", nameUser, getNameTherapist());
                    Appointment appointments = new Appointment((long) getIdTherapist(),
                            (long) idPatient, dateStart, hoursEnd, "1h",
                            getEditTextDetailsIng(), "PEDIDO");

                    new InsertAppoimentmentTask().execute(appointments);
                    insertInFireBaseDataBase(appointment);
                    //getActivity().onBackPressed();
                }
            }
        });
    }

    /******
     * insert In FireBase DataBase Appointment collection
     * @param appointment
     */
    private void insertInFireBaseDataBase(AppointmentWithName appointment) {
        Map<String, Object> appointments = new HashMap<>();
        appointments.put("idAppointment", maxIdAppointments);
        appointments.put("therapistId", appointment.getTherapistId());
        appointments.put("idPatient", appointment.getIdPatient());
        appointments.put("starTime", appointment.getStarTime());
        appointments.put("endTime", appointment.getEndTime());
        appointments.put("consultationDate", appointment.getConsultationDate());
        appointments.put("injuryDetails", appointment.getInjuryDetails());
        appointments.put("state", appointment.getState());
        appointments.put("namePatient", appointment.getNamePatient());
        appointments.put("nameTherapist", appointment.getNameTherapist());

        this.db.collection("AppointmentWithName").add(appointments);
    }

    /******
     * fetch the id value from the dataBase object
     */
    private void fetchValueIds() {
        this.db = FirebaseFirestore.getInstance();

        this.db.collection("AppointmentWithName").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                maxIdAppointments = task.getResult().size() + 1;
                Log.d("TAG", maxIdAppointments + "AppointmentWithName");
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * The type Insert appoimentment task.
     */
    public class InsertAppoimentmentTask extends AsyncTask<Appointment, Void, Long> {

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            String mes = getString(R.string.toast_save);
            Toast.makeText(getContext(), mes, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Long doInBackground(Appointment... appointments) {

            long id = EasyCareDatabase.getInstance(getContext()).appointmentDao().insert(appointments[0]);
            return id;
        }
    }

    /**
     * Validate field is filled text spinner boolean.
     *
     * @param messageErro the message erro
     * @return the boolean
     */
    public boolean validateFieldIsFilledTextSpinner(String messageErro) {
        String text = getSpinnerTherapist();
        if (Useful.isNullOrEmpty(text)) {
            spinnerTherapist.requestFocus();
            ((TextView) spinnerTherapist.getChildAt(0)).setError(messageErro);
            return true;
        }
        return false;
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link Fragment#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /*****
     * create list the Therapist for appoiment
     * and add in listSpinner
     */
    private void createListTherapist() {
        this.db.collection("therapists")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Map<String, Object> therapistData = document.getData();
                    Therapist therapist = new Therapist((Long) therapistData.get("id"),
                            (Long) therapistData.get("phoneNumber"),
                            (String) therapistData.get("name"),
                            (String) therapistData.get("email"),
                            (Long) therapistData.get("userId"));

                    listTherapist.add(therapist);
                    listSpinner.add(" " + therapist.getTherapistId() + "-" + therapist.getFullName());

                    Log.d("#######TAG", document.getId() + " => " + document.getData());
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, listSpinner);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTherapist.setAdapter(dataAdapter);
        });
    }

    /*****
     *
     * @param buttonChooseHour
     */
    private void setButtonChooseHour(final Button buttonChooseHour) {
        buttonChooseHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // user is done editing
                chooseHours();
            }
        });
    }

    /**
     * the finalization of this object to be halted, but is otherwise
     * ignored.
     *
     * @throws Throwable the {@code Exception} raised by this method
     * @jls 12.6 Finalization of Class Instances
     * @see WeakReference
     * @see PhantomReference
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void buttonShowDatePicker(final Button buttonChooseDate) {
        buttonChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDatePickerDialog();
            }
        });
    }

    /****
     *
     */
    private void chooseDatePickerDialog() {
        DatePickerDialog dataPickerDialog = new DatePickerDialog(getContext(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        dataPickerDialog.show();
    }

    /*****
     *
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "-" + month + "-" + year;
        textViewChooseDate.setText(date);

    }

    /*****
     * choose Hours for appoimentns
     */
    private void chooseHours() {
        this.calendar = Calendar.getInstance();
        this.currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        this.currentMinute = calendar.get(Calendar.MINUTE);

        this.timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                textViewChooseHour.setText(String.format("%02d:%02d", hourOfDay, minutes));

            }
        }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }

    /****
     * change the title on the toolbar
     */
    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_makeapp);
    }
}
