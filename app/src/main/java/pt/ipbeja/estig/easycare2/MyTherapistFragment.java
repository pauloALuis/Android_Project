package pt.ipbeja.estig.easycare2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ipbeja.estig.easycare2.db.entity.Therapist;

/**
 * The type My therapist fragment.
 */
public class MyTherapistFragment extends Fragment {

    private static final int REQUEST_CALL = 1;
    private JSONObject userInfoFromFile;
    private List<Therapist> listTherapist = new ArrayList<Therapist>();
    private RecyclerView recyclerView;
    private TherapistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_my_therapist, container, false);
        this.userInfoFromFile = Useful.getUserInfoFromFile(this.getContext().getFilesDir());
        try {
            this.searcheSessionsTherapiaThePatient((int) userInfoFromFile.get("patientId"), inflate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return inflate;
    }

    /****
     * load data into recyclerView
     * @param v
     */
    private void loadDataInRecyclerView(View v) {
        this.recyclerView = v.findViewById(R.id.recyclerviewListTherapist);
        this.adapter = new TherapistAdapter();
        this.recyclerView.setAdapter(adapter);
        this.adapter.setData(listTherapist);
    }

    /****
     * searches the sessions associated with the patient
     * @param patientId
     * @param view
     */
    private void searcheSessionsTherapiaThePatient(int patientId, View view) {
        {
            FirebaseFirestore referenceAppointment = FirebaseFirestore.getInstance();
            referenceAppointment.collection("AppointmentWithName")
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Map<String, Object> AppointmentsData = document.getData();

                        if ((long) AppointmentsData.get("idPatient") == patientId) {
                            searchesTherapistByIdTherapist((long) AppointmentsData.get("therapistId"), view);
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }

            });
        }
    }

    /****
     * searches for the therapist's data by id and adds the list of therapists
     * @param therapistId
     * @param view
     */
    private void searchesTherapistByIdTherapist(long therapistId, View view) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference complaintsRef = rootRef.collection("therapists");
        complaintsRef.whereEqualTo("id", therapistId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Map<String, Object> therapistData = document.getData();

                        Therapist therapist = new Therapist((Long) therapistData.get("id"),
                                (Long) therapistData.get("phoneNumber"),
                                (String) therapistData.get("name"),
                                (String) therapistData.get("email"),
                                (Long) therapistData.get("userId"));

                        listTherapist.add(therapist);
                    }

                    loadDataInRecyclerView(view);
                }
            }
        });
    }

    /****
     * change the title on the toolbar
     */
    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_my_therapist);
    }

    private void makePhoneCall(int numbers) {
        String number = numbers + "";
        if (number.trim().length() > 8) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + numbers;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {

            String m = getString(R.string.toast_incorrect_phonenuber);
            Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission done", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /************************************************************************************************************************
     * **********************************************************************************************************************
     */
    class TherapistViewHolder extends RecyclerView.ViewHolder {

        private Therapist therapist;
        private TextView contact_initials; //tvwContact_initials
        private TextView textViewNamePTherapist; //tvwCodCustomer
        private TextView therapistPhoneNum;//tvwCustomer_name
        private Button buttonCall;

        /**
         * Instantiates a new Therapist view holder.
         *
         * @param itemView the item view
         */
        public TherapistViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.contact_initials = itemView.findViewById(R.id.tvwContact_initials);
            this.textViewNamePTherapist = itemView.findViewById(R.id.textViewNamePTherapist);
            this.therapistPhoneNum = itemView.findViewById(R.id.tvwNumberPhoneTherapist);
            this.buttonCall = itemView.findViewById(R.id.buttonCallTherapist);

            itemView.setOnLongClickListener(view -> {

                return true; // Nos OnLongClickListeners temos de devolver true se tratámos o evento
            });

            /****
             * call number item
             */
            buttonCall.setOnClickListener(view -> {
                String num = "" + therapist.getPhone();
                makePhoneCall((int) therapist.getPhone());
            });
        }

        /**
         * Bind.
         *
         * @param therapist the therapist
         */
        void bind(Therapist therapist) {
            this.therapist = therapist;
            this.contact_initials.setText(generateInitials(therapist.getFullName()));
            this.therapistPhoneNum.setText(therapist.getPhone() + "");
            this.textViewNamePTherapist.setText(therapist.getFullName());
        }

        /***
         * Separate initials from name
         *
         * @param name the name
         * @return Initials name Therrapist
         */
        String generateInitials(String name) {
            String[] names = name.split(" ");
            String initials;

            if (names.length == 1) {
                initials = names[0].charAt(0) + "";
            } else {
                initials = names[0].charAt(0) + "" + names[names.length - 1].charAt(0);
            }
            return initials.toUpperCase();
        }
    }

    /**
     * The type Therapist adapter.
     */
    class TherapistAdapter extends RecyclerView.Adapter<TherapistViewHolder> {

        private List<Therapist> data = new ArrayList<>();

        /**
         * Sets data.
         *
         * @param data the data
         */
        public void setData(List<Therapist> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        /**
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         */
        @NonNull
        @Override
        public TherapistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_therapist, parent, false);
            return new TherapistViewHolder(view);

        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * have the updated adapter position.
         * <p>
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull TherapistViewHolder holder, int position) {
            Therapist customers = data.get(position);
            holder.bind(customers);
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
