package pt.ipbeja.estig.easycare2;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ipbeja.estig.easycare2.db.entity.AppointmentWithName;
import pt.ipbeja.estig.easycare2.db.entity.Patient;

/**
 * The type Location patient fragment.
 */
public class LocationPatientFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> lngArrayList = new ArrayList<LatLng>();
    private List<Patient> listPatient = new ArrayList<Patient>();
    private JSONObject userInfoFromFile;
    private Boolean checkUser;
    private List<AppointmentWithName> listAppointmentTherapist = new ArrayList<AppointmentWithName>();
    private int idTeherapsit;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_location_patient, container, false);
        this.userInfoFromFile = Useful.getUserInfoFromFile(this.getContext().getFilesDir());
        try {
            //-- read file
            this.checkUser = (Boolean) userInfoFromFile.get("isTherapist");
            this.idTeherapsit = (int) userInfoFromFile.get("therapistId");
            this.searchAppointmentTheTherapist(this.checkUser, idTeherapsit);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inflate;
    }

    /****
     * create Obtain the SupportMapFragment and get notified when the map is ready to be used.
     */
    private void createMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /****
     *
     * search the sessions associated with the therapist
     * @param checkUser
     * @param therapist
     */
    private void searchAppointmentTheTherapist(boolean checkUser, long therapist) {
        FirebaseFirestore referenceAppointment = FirebaseFirestore.getInstance();
        referenceAppointment.collection("AppointmentWithName")
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

                    if (checkUser)
                    {
                        if ((starTimeData >= Useful.getCurrentDate())
                                && (long) AppointmentsData.get("therapistId") == therapist) {
                            listAppointmentTherapist.add(cuurretAppointment);
                            loadAddressPatients((long) AppointmentsData.get("idPatient"));
                        }
                    }
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    private void loadAddressPatients(long idPatient) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference complaintsRef = rootRef.collection("patients");
        complaintsRef.whereEqualTo("id", idPatient).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Map<String, Object> patient = document.getData();

                        Patient patient1 = new Patient((long) patient.get("id"), (long) patient.get("phoneNumber"),
                                (String) patient.get("name"), (String) patient.get("email"),
                                (String) patient.get("postalCode"), (String) patient.get("address"),
                                (String) patient.get("city"), (Long) patient.get("userId"));
                        listPatient.add(patient1);
                    }
                    createMap();
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        final float zoomLevel = 6.58f;
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_cloud_circle);

        for (int i = 0; i < listPatient.size(); i++) {

            String addre = listPatient.get(i).getAddress() + ", " + listPatient.get(i).getCodPostal()
                    + ", " + listPatient.get(i).getCodPostal() + ", " + listPatient.get(i).getCity();
            LatLng latLng = this.getLatLngAddress(addre);

            mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("Cliente: " + listPatient.get(i).getFullName())
                    .snippet("X")
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }

    private LatLng getLatLngAddress(String addresssPatients) {

        double latitude = 0, longitude = 0;
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(addresssPatients, 1);
            if (addresses.size() > 0) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new LatLng(latitude, longitude);
    }

    /****
     * change the title on the toolbar
     */
    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_location);
    }
}
