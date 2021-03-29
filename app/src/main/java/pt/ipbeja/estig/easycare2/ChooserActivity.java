package pt.ipbeja.estig.easycare2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The type Chooser activity.
 */
public class ChooserActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_new_pacient;
    private Button btn_new_therapist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        btn_new_pacient = findViewById(R.id.button_new_pacient);
        btn_new_therapist = findViewById(R.id.button_new_therapist);

        btn_new_pacient.setOnClickListener(this);
        btn_new_therapist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_new_pacient) {
            startActivity(new Intent(this, RegisterPatientActivity.class));
        }
        else {
            startActivity(new Intent(this, RegistTherapistActivity.class));
        }
    }
}
