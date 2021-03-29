package pt.ipbeja.estig.easycare2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
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

import pt.ipbeja.estig.easycare2.db.entity.Therapist;
import pt.ipbeja.estig.easycare2.db.entity.User;

/**
 * The type Regist therapist activity.
 */
public class RegistTherapistActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FloatingActionButton btn_regist;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private long maxTherapistId = 0;
    private long maxUsertId = 0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_therapist);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        this.db = FirebaseFirestore.getInstance();
        this.findViewByIdObject();
        this.fetchValueIds();
        this.onClickButtonRegister();
    }

    /**
     * Sets the onclick listener for the regist button
     */
    private void onClickButtonRegister() {
        this.btn_regist.setOnClickListener(v -> {
            String s = getString(R.string.toast_enter_name);
            String num = getString(R.string.toast_enter_phone_number);
            String em = getString(R.string.toast_enter_email);
            String ps = getString(R.string.toast_enter_password);

            if (Useful.validateFieldIsFilled(editTextName, s)
                    || (Useful.validateFieldIsFilled(editTextPhoneNumber, num))
                    || (Useful.validateFieldIsFilled(editTextEmail, em))
                    || (Useful.validateFieldIsFilled(editTextPassword, ps))) {
                return;
            } else {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                registTherapist(email, password);
            }
        });
    }

    /**
     * Finds the objects by its Ids.
     */
    private void findViewByIdObject() {
        this.editTextName = findViewById(R.id.editTextName);
        this.editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        this.editTextEmail = findViewById(R.id.editTextEmail);
        this.editTextPassword = findViewById(R.id.editTextPassword);
        this.btn_regist = findViewById(R.id.buttonRegist);
    }

    /**
     * Registers the patient in the Firebase Authentication.
     * @param email user email
     * @param password user password
     */
    private void registTherapist(final String email, String password) {
        progressDialog.setMessage(getString(R.string.dialog_registering));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    User user = new User(maxUsertId + 1, email, true);
                    Therapist therapist = new Therapist(maxTherapistId + 1, Useful.convertStringOfInt(getEditTextPhoneNumber()), getEditTextName(), getEditTextEmail(), maxUsertId + 1);
                    insertTherapistInFireBaseDataBase(user, therapist);
                    writeUserToFile();
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_success_regist)
                            , Toast.LENGTH_SHORT).show();
                    finish();
                    startMainActivity();
                } else {
                    progressDialog.dismiss();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_error_account_taken), Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_error_valid_email), Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_error_stronger_password), Toast.LENGTH_LONG).show();
                    }
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
            object.put("userId", this.maxUsertId);
            object.put("isTherapist", true);
            object.put("therapistId", this.maxTherapistId);
            object.put("name", this.getEditTextName());
            object.put("email", this.getEditTextEmail());
            object.put("phoneNumber", this.getEditTextPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Starts the Main activity.
     */
    private void startMainActivity() {
        Intent starter = new Intent(RegistTherapistActivity.this, MainActivity.class);
        starter.putExtra("userId", maxUsertId + 1); // Passamos o id da nota no Intent (ver TodoDetailActivity#onCreate)
        RegistTherapistActivity.this.startActivity(starter);
    }

    /**
     * Inserts the user and therapist information in the respective Firestore collection.
     * @param user User object
     * @param therapist Therapist object
     */
    private void insertTherapistInFireBaseDataBase(User user, Therapist therapist) {
        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", user.getUserId());
        user2.put("email", user.getEmail());
        user2.put("isTherapist", user.getFlagTypeUser());

        Map<String, Object> therapist2 = new HashMap<>();
        therapist2.put("id", therapist.getTherapistId());
        therapist2.put("name", therapist.getFullName());
        therapist2.put("email", therapist.getEmail());
        therapist2.put("phoneNumber", therapist.getPhone());
        therapist2.put("userId", therapist.getUserId());

        this.db.collection("users").add(user2);
        this.db.collection("therapists").add(therapist2);
    }

    /**
     * Gets the number of documents in each collection and updates the ids for the user that is registering.
     */
    private void fetchValueIds() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                maxUsertId = task.getResult().size() + 1;
                Log.d("TAG", maxUsertId + "user");
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });

        db.collection("therapists").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                maxTherapistId = task.getResult().size() + 1;
                Log.d("TAG", maxTherapistId + "patients");
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
        return editTextName.getText().toString();
    }

    /**
     * Gets edit text phone number.
     *
     * @return the edit text phone number
     */
    public String getEditTextPhoneNumber() {
        return editTextPhoneNumber.getText().toString();
    }

    /**
     * Gets edit text email.
     *
     * @return the edit text email
     */
    public String getEditTextEmail() {
        return editTextEmail.getText().toString();
    }
}