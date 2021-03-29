package pt.ipbeja.estig.easycare2;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import pt.ipbeja.estig.easycare2.db.entity.AppointmentWithName;

/**
 * The type Home fragment.
 */
public class HomeFragment extends Fragment {

    private static boolean checkUser;
    private FirebaseDatabase fireDatabase;
    /**
     * The User reference.
     */
    public DatabaseReference userReference;
    private FirebaseFirestore referenceAppointment;
    private JSONObject userInfoFromFile;
    private List<AppointmentWithName> listAppointmentTherapist = new ArrayList<>();
    private List<AppointmentWithName> listAppointmentPatients = new ArrayList<>();
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private TextView textViewId;
    private int countRequest;
    private TextView textViewListOrder;
    private String nameUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.app_bar_main, container, false);
        this.fireDatabase = FirebaseDatabase.getInstance();
        this.userReference = fireDatabase.getReference("users");
        this.referenceAppointment = FirebaseFirestore.getInstance();
        textViewListOrder = root.findViewById(R.id.textVListOrder);

        String text = getString(R.string.title_list_request_session) + "(" + 0 + ")"
                + getString(R.string.toast_to_confirm);

        textViewListOrder.setText(text);
        this.userInfoFromFile = Useful.getUserInfoFromFile(this.getContext().getFilesDir());
        try {
            this.checkUser = (Boolean) userInfoFromFile.get("isTherapist");
            this.nameUser = (String) userInfoFromFile.get("name");

            if (this.checkUser)
                this.loadListMarkedSession(this.checkUser, (int) userInfoFromFile.get("therapistId"), root);
            else
                this.loadListMarkedSession(this.checkUser, (int) userInfoFromFile.get("patientId"), root);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }


    /**
     * Text main.
     *
     * @param count the count
     */
    public void textMain(int count) {
        textViewListOrder.setText("");
        String text = getString(R.string.title_list_request_session) + "(" + count + ")"
                + getString(R.string.toast_to_confirm);
        textViewListOrder.setText(text);

    }

    /****
     *loads the list of orders or sessions already booked
     * @param checkUser
     * @param patientId
     * @param inflante
     */
    private void loadListMarkedSession(boolean checkUser, int patientId, View inflante) {

        this.referenceAppointment.collection("AppointmentWithName")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Map<String, Object> AppointmentsData = document.getData();
                    long starTimeData = (long) AppointmentsData.get("starTime");
                    AppointmentWithName cuurretAppointment = new AppointmentWithName((long) AppointmentsData.get("idAppointment"),
                            (long) AppointmentsData.get("therapistId"), (long) AppointmentsData.get("idPatient"),
                            starTimeData, (String) AppointmentsData.get("endTime"), (String) AppointmentsData.get("consultationDate"),
                            (String) AppointmentsData.get("injuryDetails"), (String) AppointmentsData.get("state"),
                            (String) AppointmentsData.get("namePatient"), (String) AppointmentsData.get("nameTherapist"));

                    /****
                     * checks if the user is a therapist the patient
                     */
                    if (checkUser) {
                        /***
                         * lists the appointments associated with the therapist
                         */
                        if ((starTimeData >= Useful.getCurrentDate())
                                && (long) AppointmentsData.get("therapistId") == patientId) {
                            this.listAppointmentTherapist.add(cuurretAppointment);
                        }
                    } else {
                        /***
                         * lists the appointments associated with the patients
                         */
                        if ((starTimeData >= Useful.getCurrentDate()) && (long) AppointmentsData.get("idPatient") == patientId) {
                            this.listAppointmentPatients.add(cuurretAppointment);
                        }
                    }

                }
                loadDataInRecyclerView(inflante);

            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    /***
     * instantiate or adapter and load the recyclerView
     * @param view
     */
    private void loadDataInRecyclerView(View view) {
        this.recyclerView = view.findViewById(R.id.recyclerviewListAppoitment);
        this.adapter = new AppointmentAdapter();
        this.recyclerView.setAdapter(adapter);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

        if (checkUser) adapter.setData(listAppointmentTherapist);
        else adapter.setData(listAppointmentPatients);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
    }


    /**
     * The type Appointment view holder.
     */
    class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private AppointmentWithName appointments;
        private TextView textViewNamePatients; //tvwContact_initials
        private TextView tvwDataSessionAppoint; //tvwCodCustomer
        private ImageView imageVRequest; //
        private Button buttonConfirmSession2;//
        private Button buttonCancel;//

        /**
         * Instantiates a new Appointment view holder.
         *
         * @param itemView the item view
         */
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewNamePatients = itemView.findViewById(R.id.textViewNamePTherapist);
            this.tvwDataSessionAppoint = itemView.findViewById(R.id.tvwNumberPhoneTherapist);
            this.imageVRequest = itemView.findViewById(R.id.imageVRequest);
            textViewId = itemView.findViewById(R.id.txvId);
            this.buttonConfirmSession2 = itemView.findViewById(R.id.buttonCallTherapist);
            this.buttonCancel = itemView.findViewById(R.id.buttonCancelSession);


                 this.buttonConfirmSession2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = Integer.parseInt(textViewId.getText().toString());
                    update(id);
                    notifyAdapter();


                }
            });

            /*****
             * cancel appointment
             */
            this.buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = Integer.parseInt(textViewId.getText().toString());
                    delete(id);
                    notifyAdapter();
                }
            });
        }

        /**
         * Bind.
         *
         * @param appointment the appointment
         */
        void bind(AppointmentWithName appointment) {
            this.appointments = appointment;
            this.tvwDataSessionAppoint.setText(Useful.formatDate(appointment.getStarTime()));
            textViewId.setText(appointment.getIdAppointment() + "");

            if (checkUser) {
                /*** Change menu drawer menu therapist  ****/
                this.textViewNamePatients.setText(appointment.getNamePatient());
            } else {
                /*** Change menu drawer menu therapist  ****/
                this.textViewNamePatients.setText(appointment.getNameTherapist());
            }

            if (appointment.getState().equalsIgnoreCase("ACEITE")) {
                this.imageVRequest.setImageBitmap(null);
                this.imageVRequest.setImageResource(R.drawable.ic_done_all_black_24dp);
                buttonConfirmSession2.setVisibility(View.INVISIBLE);

            } else if (appointment.getState().equalsIgnoreCase("PEDIDO")) {
                countRequest++;
            }

            textMain(countRequest);
        }

        void notifyAdapter(){
            //delete( pos);
            //adapter.notifyItemRemoved(i);
            adapter.notifyDataSetChanged();
        }

        /**
         * Update.
         *
         * @param idApp the id app
         */
        void update(int idApp) {
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            CollectionReference complaintsRef = rootRef.collection("AppointmentWithName");
            complaintsRef.whereEqualTo("idAppointment", idApp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<Object, String> map = new HashMap<>();
                            map.put("state", "ACEITE");
                            complaintsRef.document(document.getId()).set(map, SetOptions.merge());
                        }
                        buttonConfirmSession2.setVisibility(View.INVISIBLE);
                        String messe = getString(R.string.button_save_changes);
                        Toast.makeText(getContext(), messe, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        /**
         * Delete.
         *
         * @param idApp the id app
         */
        void delete(int idApp) {
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            CollectionReference complaintsRef = rootRef.collection("AppointmentWithName");
            complaintsRef.whereEqualTo("idAppointment", idApp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        complaintsRef.document().delete();
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            complaintsRef.document(document.getId()).delete();
                        }
                    }
                }
            });
        }
    }

    /**
     * The type Appointment adapter.
     */
    class AppointmentAdapter extends RecyclerView.Adapter<AppointmentViewHolder> {

        private List<AppointmentWithName> data = new ArrayList<>();

        /**
         * Sets data.
         *
         * @param data the data
         */
        public void setData(List<AppointmentWithName> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_appointments, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            AppointmentWithName customers = data.get(position);
            holder.bind(customers);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


    /**
     * The Item touch helper.
     */
    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int i = viewHolder.getAdapterPosition();
            int pos = Integer.parseInt(textViewId.getText().toString());
            //final Customers cust  = EasyCareDataBase.getINSTANCE(getContext()).customerDao().get(pos);


            switch (direction) {
                //move right to left delete
                case ItemTouchHelper.LEFT:

                    //delete( pos);
                    adapter.notifyItemRemoved(i);
                    adapter.notifyDataSetChanged();

                    // EasyCareDataBase.getINSTANCE(getContext()).customerDao().delete(cust);
                    // snackbarUndoCustormer( getString(R.string.undo), cust.getNameCustomer(), cust );
                    break;
                //move left to right edit
                case ItemTouchHelper.RIGHT:
                    adapter.notifyItemRemoved(i);
                    adapter.notifyDataSetChanged();
                    // startUpdateCustomer(cust);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    //delete change color and icon
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.deleteColor))
                    .addSwipeLeftActionIcon(R.drawable.ic_trash)

                    //edit change color and icon
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.YelloColor))
                    .addSwipeRightActionIcon(R.drawable.ic_edit)

                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
