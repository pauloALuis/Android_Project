package pt.ipbeja.estig.easycare2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * The type Profile patient fragment.
 */
public class ProfilePatientFragment extends Fragment implements View.OnClickListener {


    private static final int  SELECT_FILE = 0;
    private static final String IMAGE_GALERY ="image/*" ;
    private static final int REQUEST = 23;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static String USER_NAME ;

    private FloatingActionButton editButton;
    private FloatingActionButton saveChangesButton;
    private FloatingActionButton cancelButton;
    private EditText name;
    private EditText phoneNumber;
    private EditText address;
    private EditText postalCode;
    private EditText city;
    private EditText email;

    private JSONObject userInfoFromFile;
    private Button btnAlterPicProfilePatients;
    private ImageView imageViewProfilePatient;
    private OutputStream outputStream;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_profile_patient, container, false);
        setHasOptionsMenu(true);
        this.loadProfile(inflate);

        if(Build.VERSION.SDK_INT >= this.REQUEST){
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        this.onClickButtonAlterPic(this.btnAlterPicProfilePatients);
        loadPicImageView();
        return inflate;
    }


    /*****
     *      * handles the click event the button lterPicProfilePatients , calls the method DisplayOpcionFindImage to show
     * @param btnAlterPicProfilePatients
     */
    private void onClickButtonAlterPic(Button btnAlterPicProfilePatients) {
        btnAlterPicProfilePatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayOpcionFindImage();
            }
        });
    }



    /******
     * upload photo in directory DCIM/EASY2 FORM ImageViewProfile
     * and resize photo
     */
    private void  loadPicImageView(){
        File downLoadsFolder =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Uri file = Uri.fromFile(new File(downLoadsFolder, "easy2/" + this.USER_NAME + ".png"));

        if(file.toString() != null && file.toString().length() > 0){
            Picasso.with(getContext()).load(file).
                    resize(120, 130).into(imageViewProfilePatient);
            Picasso.with(getContext()).load(file).placeholder(R.drawable.progress_animation).into(imageViewProfilePatient);
        }
        else {
            Toast.makeText(getContext(), "Empty URI ", Toast.LENGTH_LONG).show();

        }
    }


    /******
     * Save photo in directory DCIM/EASY2
     */
    private void saveImageInStorage() {
        Bitmap bitmap = ((BitmapDrawable) imageViewProfilePatient.getDrawable()).getBitmap();

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/easy2");
        dir.mkdir();
        String nameImage = this.USER_NAME + ".PNG";

        File file1 = new File(dir, nameImage);
        try {
            outputStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            // e.printStackTrace();
            Toast.makeText(getContext(),"ERRO: !!" + e.getMessage(), Toast.LENGTH_LONG).show();

        }
        finally {
            String t = getString(R.string.toast_save);
            Toast.makeText(getContext(),t, Toast.LENGTH_LONG).show();

        }

    }


    /****
     * Shows the DialogInterface and tries to call the chosen actions
     */
    private void DisplayOpcionFindImage()
    {
        String texts = getString(R.string. toast_add_image);
        final CharSequence[] items = {"Camera","Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(texts);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera"))
                {
                    Toast.makeText(getContext(), "not implement",Toast.LENGTH_LONG).show();
                }
                else if (items[which].equals("Gallery")) {
                    actionChoosePhotoinGallery();

                }
                else if (items[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /****
     * choose photo in gallery
     */
    private void actionChoosePhotoinGallery() {
        String text = getString(R.string. toast_select_file);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(IMAGE_GALERY);
        startActivityForResult(intent.createChooser(intent,text ), SELECT_FILE);

    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            }
            else if(requestCode== SELECT_FILE){
                //capture gallery
                Uri selectUri = data.getData();
                imageViewProfilePatient.setImageURI(selectUri);
                saveImageInStorage();

            }
        }
    }




    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_profile_patient);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_change_password:
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Catches the Edit texts and buttons, and sets on click listeners for the buttons.
     * @param view Parent view
     */
    private void loadProfile(View view) {

        this.userInfoFromFile = Useful.getUserInfoFromFile(getContext().getFilesDir());
        this.name = view.findViewById(R.id.editTextName);
        this.phoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        this.address = view.findViewById(R.id.editTextAddress);
        this.postalCode = view.findViewById(R.id.editTextPostalCode);
        this.city = view.findViewById(R.id.editTextCity);
        this.email = view.findViewById(R.id.editTextEmail);
        this.saveChangesButton = view.findViewById(R.id.button_save_changes);
        this.cancelButton = view.findViewById(R.id.button_cancel);
        this.editButton = view.findViewById(R.id.button_edit);
        this.btnAlterPicProfilePatients = view.findViewById(R.id.btnAlterPicProfilePatients);
        this.imageViewProfilePatient = view.findViewById(R.id.imageViewProfilePatient);


        this.showUserInfo();

        this.editButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.saveChangesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save_changes) {

            if (Useful.validateFieldIsFilled(this.name, getString(R.string.toast_enter_name))
                    || Useful.validateFieldIsFilled(this.phoneNumber, getString(R.string.toast_enter_phone_number))
                    || Useful.validateFieldIsFilled(this.address, getString(R.string.toast_enter_address))
                    || Useful.validateFieldIsFilled(this.postalCode, getString(R.string.toast_enter_postal_code))
                    || Useful.validateCodPostal(this.postalCode)
                    || Useful.validateFieldIsFilled(this.city, getString(R.string.toast_enter_city))) {

                return;
            } else {

                this.saveChangesButton.setVisibility(View.INVISIBLE);
                this.cancelButton.setVisibility(View.INVISIBLE);
                this.editButton.setVisibility(View.VISIBLE);
                this.disableEditTexts();
                this.updateUserInfo();
            }
        } else if (v.getId() == R.id.button_edit) {

            this.saveChangesButton.setVisibility(View.VISIBLE);
            this.cancelButton.setVisibility(View.VISIBLE);
            this.editButton.setVisibility(View.INVISIBLE);
            this.enableEditTexts();

        } else if (v.getId() == R.id.button_cancel) {

            this.saveChangesButton.setVisibility(View.INVISIBLE);
            this.cancelButton.setVisibility(View.INVISIBLE);
            this.editButton.setVisibility(View.VISIBLE);
            this.showUserInfo();
            this.disableEditTexts();
        }
    }

    /**
     * Updates the the user info when the save changes button is pressed.
     */
    private void updateUserInfo() {

        try {
            this.userInfoFromFile.put("name", this.name.getText().toString().trim());
            this.userInfoFromFile.put("phoneNumber", this.phoneNumber.toString().trim());
            this.userInfoFromFile.put("address", this.address.toString().trim());
            this.userInfoFromFile.put("postalCode", this.postalCode.toString().trim());
            this.userInfoFromFile.put("city", this.city.toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Useful.writeUserTofile(getContext(), this.userInfoFromFile);
    }

    /**
     * Disables the Edit texts.
     */
    private void disableEditTexts() {

        this.name.setEnabled(false);
        this.phoneNumber.setEnabled(false);
        this.address.setEnabled(false);
        this.postalCode.setEnabled(false);
        this.city.setEnabled(false);
    }

    /**
     * Enables the Edit texts.
     */
    private void enableEditTexts() {

        this.name.setEnabled(true);
        this.phoneNumber.setEnabled(true);
        this.address.setEnabled(true);
        this.postalCode.setEnabled(true);
        this.city.setEnabled(true);
    }

    /**
     * Displays the user info in the Edit texts.
     */
    private void showUserInfo() {

        try {

            String nam = userInfoFromFile.getString("email");
            this.USER_NAME = nam;
            this.name.setText(userInfoFromFile.getString("name"));
            this.phoneNumber.setText(String.valueOf(userInfoFromFile.getInt("phoneNumber")));
            this.address.setText(userInfoFromFile.getString("address"));
            this.postalCode.setText(userInfoFromFile.getString("postalCode"));
            this.city.setText(userInfoFromFile.getString("city"));
            this.email.setText(nam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
