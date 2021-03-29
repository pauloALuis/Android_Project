package pt.ipbeja.estig.easycare2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import pt.ipbeja.estig.easycare2.db.entity.Patient;
import pt.ipbeja.estig.easycare2.db.entity.Therapist;
import pt.ipbeja.estig.easycare2.db.entity.User;

/**
 * The type Log in activity.
 */
public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FloatingActionButton logInButton;
    private FloatingActionButton registerButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private User user;
    private Therapist therapist;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        this.db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_log_in);
        progressDialog = new ProgressDialog(this);

        logInButton = findViewById(R.id.buttonLogIn);
        registerButton = findViewById(R.id.buttonRegist);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        logInButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == logInButton) {
            logIn();
        } else if (v == registerButton) {
            startActivity(new Intent(this, ChooserActivity.class));
        }
    }

    /**
     * Performs the user log in.
     */
    private void logIn() {

        if (Useful.validateFieldIsFilled(editTextEmail, getString(R.string.toast_enter_email)) ||
                Useful.validateFieldIsFilled(editTextPassword, getString(R.string.toast_enter_password))) {
            return;
        }

        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        progressDialog.setMessage(getString(R.string.dialog_login_in));
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            this.progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_success_log_in), Toast.LENGTH_SHORT).show();
                getUserByEmail(email);
            }
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_error_wrong_password), Toast.LENGTH_LONG).show();

            } else if (e instanceof FirebaseAuthInvalidUserException) {

                String errorCode =
                        ((FirebaseAuthInvalidUserException) e).getErrorCode();

                if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_error_missing_account), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }

    /**
     * Accesses the Firestore and creates a User given its email
     *
     * @param email user email
     */
    private void getUserByEmail(String email) {
        this.db.collection("users").
                whereEqualTo("email", email).
                get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Map<String, Object> userData = document.getData();
                    user = new User((Long) userData.get("id"), (String) userData.get("email"),
                            (Boolean) userData.get("isTherapist"));
                    userToJsonObject();
                    Log.d("#userByEmail", document.getId() + " => " + document.getData());
                }
            } else {

                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Writes the user information to a file, given an JSON object with the information to write.
     *
     * @param object JSON object containing the user information
     */
    private void writeUserToFile(JSONObject object) {
        File filesDir = new File(this.getFilesDir(), "userDir");
        if (!filesDir.exists()) {

            filesDir.mkdir();
        }
        try {
            File file = new File(filesDir, "userInfoFile");
            FileWriter writer = new FileWriter(file);
            String user = object.toString();
            writer.write(user);
            writer.close();
            Log.d("#writeUserToFile", user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accesses the Firestore to get current user information(therapist or patient), makes a JSON
     * object with it, passes that object to the function writeUserToFile() and starts the main Activity.
     */
    private void userToJsonObject() {
        JSONObject object = new JSONObject();
        if (this.user.getFlagTypeUser()) {
            this.db.collection("therapists").whereEqualTo("userId", this.user.getUserId())
                    .get().addOnCompleteListener(task -> {
                this.progressDialog.dismiss();
                if (task.isSuccessful()) {

                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Map<String, Object> therapistData = document.getData();
                        therapist = new Therapist((Long) therapistData.get("id"),
                                (Long) therapistData.get("phoneNumber"),
                                (String) therapistData.get("name"),
                                (String) therapistData.get("email"),
                                (Long) therapistData.get("userId"));
                        Log.d("#therapist:", document.getId() + " => " + document.getData());
                    }
                    try {
                        object.put("userId", this.user.getUserId());
                        object.put("isTherapist", true);
                        object.put("therapistId", therapist.getTherapistId());
                        object.put("name", therapist.getFullName());
                        object.put("phoneNumber", therapist.getPhone());
                        object.put("email", therapist.getEmail());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    this.writeUserToFile(object);
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            });

        } else {
            this.db.collection("patients").whereEqualTo("userId", this.user.getUserId())
                    .get().addOnCompleteListener(task -> {
                this.progressDialog.dismiss();
                if (task.isSuccessful()) {

                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Map<String, Object> patientData = document.getData();
                        patient = new Patient((long) patientData.get("id"),
                                (long) patientData.get("phoneNumber"),
                                (String) patientData.get("name"),
                                (String) patientData.get("email"),
                                (String) patientData.get("postalCode"),
                                (String) patientData.get("address"),
                                (String) patientData.get("city"),
                                (long) patientData.get("userId"));
                        Log.d("#patient:", document.getId() + " => " + document.getData());
                    }

                    try {
                        object.put("userId", this.user.getUserId());
                        object.put("isTherapist", false);
                        object.put("patientId", patient.getIdPatient());
                        object.put("name", patient.getFullName());
                        object.put("phoneNumber", patient.getPhone());
                        object.put("address", patient.getAddress());
                        object.put("postalCode", patient.getCodPostal());
                        object.put("city", patient.getCity());
                        object.put("email", patient.getEmailPatient());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    this.writeUserToFile(object);
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            });
        }
    }
}
