package pt.ipbeja.estig.easycare2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.ipbeja.estig.easycare2.db.entity.Patient;
import pt.ipbeja.estig.easycare2.db.entity.User;

/**
 * The type Register patient activity.
 */
public class RegisterPatientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextAddress;
    private EditText editTextPostalCode;
    private EditText editTextCity;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FloatingActionButton btnRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private long maxPatientId;
    private long maxUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pacient);
        this.findViewByIdObject();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        this.fetchValueIds();
        this.onClickButtonRegister();
    }

    /**
     * Sets the onclick listener for the register button.
     */
    private void onClickButtonRegister() {

        btnRegister.setOnClickListener(v -> {

            if (Useful.validateFieldIsFilled(editTextName, getString(R.string.toast_enter_email))
                    || Useful.validateFieldIsFilled(editTextPhoneNumber, getString(R.string.toast_enter_phone_number))
                    || Useful.validateFieldIsFilled(editTextAddress, getString(R.string.toast_enter_address))
                    || Useful.validateFieldIsFilled(editTextPostalCode, getString(R.string.toast_enter_postal_code))
                    || Useful.validateCodPostal(editTextPostalCode)
                    || Useful.validateFieldIsFilled(editTextCity, getString(R.string.toast_enter_city))
                    || Useful.validateFieldIsFilled(editTextEmail, getString(R.string.toast_enter_email))
                    || Useful.validateFieldIsFilled(editTextPassword, getString(R.string.toast_enter_password))
            ) {
                return;
            } else {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                registPacient(email, password);
            }
        });
    }

    /**
     * Finds the objects views by its id.
     */
    private void findViewByIdObject() {
        this.editTextName = findViewById(R.id.editTextName);
        this.editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        this.editTextAddress = findViewById(R.id.editTextAddress);
        this.editTextPostalCode = findViewById(R.id.editTextPostalCode);
        this.editTextCity = findViewById(R.id.editTextCity);
        this.editTextEmail = findViewById(R.id.editTextEmail);
        this.editTextPassword = findViewById(R.id.editTextPassword);
        this.btnRegister = findViewById(R.id.buttonRegist);
        this.editTextPostalCode.addTextChangedListener(addTextCodPostal);

    }

    /**
     * The Add text cod postal.
     */
    TextWatcher addTextCodPostal = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editTextPostalCode.getText().toString();
            /****
             * if the number of characters is greater than 4, add -
             */
            if (text.length() == 4) {
                editTextPostalCode.setText(text + "-");
                return;
            }
        }
    };

    /**
     * Registers the patient in the Firebase Authentication.
     * @param email user email
     * @param password user password
     */
    private void registPacient(final String email, String password) {
        this.progressDialog.setMessage(getString(R.string.dialog_registering));
        this.progressDialog.show();

        this.firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        User user = new User(maxUserId, email, false);
                        Patient patient = new Patient(maxPatientId,
                                Useful.convertStringOfInt(getEditTextPhoneNumber()),
                                getEditTextName(), getEditTextEmail(),
                                getEditTextPostalCode(), getEditTextAddress(), getEditTextCity(), maxUserId);
                        insertPatientInFireBase(user, patient);
                        writeUserToFile();
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_success_regist)
                                , Toast.LENGTH_SHORT).show();

                        finish();
                        starMainAtivity();
                    } else {
                        progressDialog.dismiss();
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_error_account_taken)
                                    , Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_error_valid_email)
                                    , Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_error_stronger_password)
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Writes the current user information to file.
     */
    private void writeUserToFile() {

        File filesDir = new File(this.getFilesDir(), "userDir");
        if (!filesDir.exists()) {
            filesDir.mkdir();
        }
        try {
            File file = new File(filesDir, "userInfoFile");
            FileWriter writer = new FileWriter(file);
            String user = this.userToJsonObject().toString();
            Log.d("##### user:", user);
            writer.write(user);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a JSON object containing the current user information.
     * @return JSON object
     */
    private JSONObject userToJsonObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", this.maxUserId);
            object.put("isTherapist", false);
            object.put("patientId", this.maxPatientId);
            object.put("name", this.getEditTextName());
            object.put("phoneNumber", this.getEditTextPhoneNumber());
            object.put("address", this.getEditTextAddress());
            object.put("postalCode", this.getEditTextPostalCode());
            object.put("city", this.getEditTextCity());
            object.put("email", this.getEditTextEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Starts the Main activity.
     */
    private void starMainAtivity() {
        Intent starter = new Intent(RegisterPatientActivity.this, MainActivity.class);
        starter.putExtra("userId", maxUserId);
        RegisterPatientActivity.this.startActivity(starter);
    }

    /**
     * Inserts the user and patient information in the respective Firestore collection.
     * @param user User object
     * @param patient Patient object
     */
    private void insertPatientInFireBase(User user, Patient patient) {
        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", user.getUserId());
        user2.put("email", user.getEmail());
        user2.put("isTherapist", user.getFlagTypeUser());

        Map<String, Object> patient2 = new HashMap<>();
        patient2.put("id", patient.getIdPatient());
        patient2.put("name", patient.getFullName());
        patient2.put("email", patient.getEmailPatient());
        patient2.put("phoneNumber", patient.getPhone());
        patient2.put("postalCode", patient.getCodPostal());
        patient2.put("address", patient.getAddress());
        patient2.put("city", patient.getCity());
        patient2.put("userId", patient.getUserId());
        this.db.collection("users").add(user2);
        this.db.collection("patients").add(patient2);
    }

    /**
     * Gets the number of documents in each collection and updates the ids for the user that is registering.
     */
    private void fetchValueIds() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                maxUserId = task.getResult().size() + 1;
                Log.d("TAG", maxUserId + "user");
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });

        db.collection("patients").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                maxPatientId = task.getResult().size() + 1;
                Log.d("TAG", maxPatientId + "patients");
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Gets edit text name.
     *
     * @return the edit text name
     */
    public String getEditTextName() {
        return this.editTextName.getText().toString().trim();
    }

    /**
     * Gets edit text phone number.
     *
     * @return the edit text phone number
     */
    public String getEditTextPhoneNumber() {
        return editTextPhoneNumber.getText().toString().trim();
    }

    /**
     * Gets edit text address.
     *
     * @return the edit text address
     */
    public String getEditTextAddress() {
        return this.editTextAddress.getText().toString().trim();
    }

    /**
     * Gets edit text postal code.
     *
     * @return the edit text postal code
     */
    public String getEditTextPostalCode() {
        return this.editTextPostalCode.getText().toString().trim();
    }

    /**
     * Gets edit text city.
     *
     * @return the edit text city
     */
    public String getEditTextCity() {
        return this.editTextCity.getText().toString().trim();
    }

    /**
     * Gets edit text email.
     *
     * @return the edit text email
     */
    public String getEditTextEmail() {
        return this.editTextEmail.getText().toString().trim();
    }
}
