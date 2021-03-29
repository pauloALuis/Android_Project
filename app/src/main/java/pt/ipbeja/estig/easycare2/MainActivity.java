package pt.ipbeja.estig.easycare2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long userId;
    private static boolean checkUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireDatabase;
    /**
     * The User reference.
     */
    public DatabaseReference userReference;
    private FirebaseFirestore referenceAppointment;
    private JSONObject userInfoFromFile;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private ProgressDialog progressDialog;
    private TextView textViewUseName;
    private TextView textViewEmailUse;
    private FloatingActionButton buttonChangePic;
    private ImageView imagePorfile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getLongExtra("userId", 1);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.fireDatabase = FirebaseDatabase.getInstance();
        this.userReference = fireDatabase.getReference("users");
        this.referenceAppointment = FirebaseFirestore.getInstance();
        this.userInfoFromFile = Useful.getUserInfoFromFile(this.getFilesDir());

        try {
            this.checkUser = (Boolean) userInfoFromFile.get("isTherapist");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.createMenuDrawer(savedInstanceState);
        this.updateNavHeader();
        //this.loadPicImageView();
    }


    /**
     * Called when the Fragment is visible to the user.  This is generally
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
       this.loadPicImageView();
    }

    /******
     * update NavigationView  Header
     * update textViewEmailUse, imagePorfile and textViewUseName
     */
    private void updateNavHeader() {
        View headView = this.navigationView.getHeaderView(0);
        this.textViewEmailUse = headView.findViewById(R.id.tViewEmailUser);
        this.textViewUseName = headView.findViewById(R.id.tViewNameUser);
        this.imagePorfile = headView.findViewById(R.id.imagPorfile);
        this.buttonChangePic = headView.findViewById(R.id.btnAlterPicProfilePatients);

        try {
            this.setTextViewUserName(this.userInfoFromFile.get("name").toString());
            this.setTextViewEmailUser(this.userInfoFromFile.get("email").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets text view user name.
     *
     * @return the text view user name
     */
    public String getTextViewUserName() {
        return textViewUseName.getText().toString();
    }

    /**
     * Sets text view user name.
     *
     * @param userName the user name
     */
    public void setTextViewUserName(String userName) {
        this.textViewUseName.setText(userName);
    }

    /**
     * Gets text view email user.
     *
     * @return the text view email user
     */
    public String getTextViewEmailUser() {
        return textViewEmailUse.getText().toString();
    }

    /**
     * Sets text view email user.
     *
     * @param emailUser the email user
     */
    public void setTextViewEmailUser(String emailUser) {
        this.textViewEmailUse.setText(emailUser);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /***
     * create Menu Drawer
     * @param savedInstanceState
     */
    private void createMenuDrawer(Bundle savedInstanceState) {

        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer, R.string.closeDrawer);
        this.mDrawerLayout.addDrawerListener(mToggle);
        this.mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.navigationView = findViewById(R.id.nav_view);

        if (this.checkUser) {
            /*** Change menu drawer menu therapist  ****/
            this.navigationView.getMenu().clear();
            this.navigationView.inflateMenu(R.menu.drawer_menu_therapist);
        } else {
            /*** Change menu drawer menu patient  ****/
            this.navigationView.getMenu().clear();
            this.navigationView.inflateMenu(R.menu.drawer_menu_patient);
        }

        this.navigationView.setNavigationItemSelectedListener(this);
        this.savedInstanceState(savedInstanceState);
    }

    /********
     *If you go back to the Activity that was killed, the persisted state will be passed to onCreate
     * of the Activity in the savedInstanceState parameter. This will allow you to restore the Activity to its last state.
     * @param savedInstanceState
     */
    private void savedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            this.navigationView.setCheckedItem(R.id.item_home);
        }
    }

    /****
     *
     * receive the menu element
     * @param item item
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (this.mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        this.mDrawerLayout.closeDrawer(GravityCompat.START);

        if (id == R.id.item_home) {
            this.setFragment(new HomeFragment());
            return true;
        } else if (id == R.id.item_profile_therapist) {
            this.setFragment(new ProfileTherapistFragment());
            return true;
        } else if (id == R.id.item_location_patient) {
            this.setFragment(new LocationPatientFragment());
            return true;
        } else if (id == R.id.item_profile_patient) {
            this.setFragment(new ProfilePatientFragment());
            return true;
        } else if (id == R.id.item_shedule_therapist) {
            this.setFragment(new ScheduleTherapistFragment());
            return true;
        } else if (id == R.id.item_my_therapist) {
            this.setFragment(new MyTherapistFragment());
            return true;
        } else if (id == R.id.item_make_appointment)
        {
            this.setFragment(new MakeAppointmentFragment());
            return true;
        } else if (id == R.id.item_location_patient) {

            this.setFragment(new LocationPatientFragment());
            return true;
        } else if (id == R.id.item_profile_patient) {

            this.setFragment(new ProfilePatientFragment());
            return true;
        } else if (id == R.id.item_shedule_therapist) {

            this.setFragment(new ScheduleTherapistFragment());
            return true;
        } else if (id == R.id.item_my_therapist) {

            Toast.makeText(this, " My therapist implement", Toast.LENGTH_SHORT).show();
            this.setFragment(new MyTherapistFragment());
            return true;
        } else if (id == R.id.item_make_appointment) {

            this.setFragment(new MakeAppointmentFragment());
            return true;
        } else if (id == R.id.item_log_out) {

            this.logOut();
        }

        this.loadPicImageView();

        return false;
    }

    /**
     * Terminates the current user session and starts the Log In activity.
     */
    private void logOut() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getText(R.string.dialog_signing_in));

        try {
            firebaseAuth.signOut();
            Toast.makeText(getApplicationContext(), getText(R.string.toast_success_log_out), Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    /******imageViewProfilePatient
     * upload photo in directory DCIM/EASY2 FORM ImageViewProfile
     * and resize photo
     */
    private void  loadPicImageView(){
        File downLoadsFolder =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Uri file = Uri.fromFile(new File(downLoadsFolder, "easy2/" + this.getTextViewEmailUser() + ".png"));

        if(file.toString() != null && file.toString().length() > 0){

            Picasso.with(getApplicationContext()).load(file).placeholder(R.drawable.progress_animation).into(imagePorfile);

            Picasso.with(MainActivity.this).load(file).
                    resize(200, 170).into(imagePorfile);


        }
        else {
            Toast.makeText(MainActivity.this, "Empty URI ", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*****
     * open the fragment inserted in the parameter
     * @param fragmentOpen the fragment
     */
    public void setFragment(Fragment fragmentOpen) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentOpen);
        fragmentTransaction.commit();
    }

    /**
     * Image click.
     *
     * @param view the view
     */
    public void imageClick(View view) {
    }


    /**
     * Messagem screen.
     *
     * @param message the message
     */
    public void messagemScreen(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}