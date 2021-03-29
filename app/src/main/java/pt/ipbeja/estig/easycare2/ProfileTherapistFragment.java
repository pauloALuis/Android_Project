package pt.ipbeja.estig.easycare2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
 * The type Profile therapist fragment.
 */
public class ProfileTherapistFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton editButton;
    private FloatingActionButton saveChangesButton;
    private Button btnAlterPicProfileTherapist;

    private FloatingActionButton cancelButton;
    private EditText name;
    private EditText phoneNumber;
    private EditText email;
    private JSONObject userInfoFromFile;
    private ImageView imageViewProfilePTherapist;
    private String nameUser;

    private static final int  SELECT_FILE = 0;
    private static final String IMAGE_GALERY ="image/*" ;
    private static final int REQUEST = 23;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private OutputStream outputStream;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_profile_therapist, container, false);
        setHasOptionsMenu(true);


        this.btnAlterPicProfileTherapist = inflate.findViewById(R.id.btnPicProfileTherapist);
        this.loadProfile(inflate);
        this.onClickButtonAlterPic(this.btnAlterPicProfileTherapist);
        loadPicImageView();
        return inflate;
    }

    /****
     * change the title on the toolbar
     */
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_profile_therapist);
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
        this.email = view.findViewById(R.id.editTextEmail);
        this.saveChangesButton = view.findViewById(R.id.button_save_changes);
        this.cancelButton = view.findViewById(R.id.button_cancel);
        this.editButton = view.findViewById(R.id.button_edit);
        this.imageViewProfilePTherapist = view.findViewById(R.id.imageViewTherapist);


        this.showUserInfo();

        this.editButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.saveChangesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save_changes) {

            if (Useful.validateFieldIsFilled(this.name, getString(R.string.toast_enter_name))
                    || Useful.validateFieldIsFilled(this.phoneNumber, getString(R.string.toast_enter_phone_number))) {

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Useful.writeUserTofile(getContext(), this.userInfoFromFile);
    }

    /**
     * Enables the Edit texts.
     */
    private void enableEditTexts() {
        this.name.setEnabled(true);
        this.phoneNumber.setEnabled(true);
    }

    /**
     * Disables the Edit texts.
     */
    private void disableEditTexts() {
        this.name.setEnabled(false);
        this.phoneNumber.setEnabled(false);
    }

    /**
     * Displays the user info in the Edit texts.
     */
    private void showUserInfo() {
        try {
            this.name.setText(userInfoFromFile.getString("name"));
            this.phoneNumber.setText(String.valueOf(userInfoFromFile.getInt("phoneNumber")));
            this.nameUser = userInfoFromFile.getString("email");
            this.email.setText(this.nameUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    /******
     * upload photo in directory DCIM/EASY2 FORM ImageViewProfile
     * and resize photo
     */
    private void  loadPicImageView(){
        File downLoadsFolder =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Uri file = Uri.fromFile(new File(downLoadsFolder, "easy2/" + this.nameUser + ".png"));

        if(file.toString() != null && file.toString().length() > 0){
            Picasso.with(getContext()).load(file).
                    resize(120, 130).into(imageViewProfilePTherapist);
            Picasso.with(getContext()).load(file).placeholder(R.drawable.progress_animation).into(imageViewProfilePTherapist);
        }
        else {
            Toast.makeText(getContext(), "Empty URI ", Toast.LENGTH_LONG).show();

        }
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
     * Save photo in directory DCIM/EASY2
     */
    private void saveImageInStorage() {
        Bitmap bitmap = ((BitmapDrawable) imageViewProfilePTherapist.getDrawable()).getBitmap();

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/easy2");
        dir.mkdir();
        String nameImage = this.nameUser + ".PNG";

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
                imageViewProfilePTherapist.setImageURI(selectUri);
                saveImageInStorage();

            }
        }
    }



}
