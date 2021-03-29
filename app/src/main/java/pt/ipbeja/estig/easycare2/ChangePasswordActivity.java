package pt.ipbeja.estig.easycare2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The type Change password activity.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editText_current_password;
    private EditText editText_new_password;
    private EditText editText_confirm_password;
    private Button button_save_changes;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        this.editText_current_password = findViewById(R.id.editText_current_password);
        this.editText_new_password = findViewById(R.id.editText_new_password);
        this.editText_confirm_password = findViewById(R.id.editText_confirm_new_password);
        this.button_save_changes = findViewById(R.id.button_save_changes);
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.button_save_changes.setOnClickListener(v -> {

            String current = editText_current_password.getText().toString().trim();
            String new_ = editText_new_password.getText().toString().trim();
            String confirm = editText_confirm_password.getText().toString().trim();
            if (TextUtils.isEmpty(current)) {
                Toast.makeText(getApplicationContext(), "Please enter current password!",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(new_)) {
                Toast.makeText(getApplicationContext(), "Please enter new password!",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(confirm)) {
                Toast.makeText(getApplicationContext(), "Please confirm new password!",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (!TextUtils.equals(new_, confirm)) {
                Toast.makeText(getApplicationContext(), "The passwords entered don't match!",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                changePassword(current, new_);
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*****
     * validates the Password and changes Password
     * @param current
     * @param new_
     */
     private void changePassword(String current, final String new_) {

        this.user = firebaseAuth.getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, current);
        this.dialog = new ProgressDialog(this);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                dialog.dismiss();
                if (task.isSuccessful()) {
                    dialog.setMessage("Changing password...");
                    dialog.show();
                    user.updatePassword(new_).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password changed successfully!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong! Please try again later.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error! Wrong password!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
