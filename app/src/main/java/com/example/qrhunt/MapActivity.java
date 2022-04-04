package com.example.qrhunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The class is for control the map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    ArrayList<Double> gpsLatLng;
    ArrayList<GameQRCode> QRCodes;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Players");
    private String TAG = "players";

    /**
     * Called by the android system to build up the fragment;
     *
     * @param savedInstanceState
     *      This is the context record of the upper level activity, is used to access the view;
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        // get the map's starting point from intent extras
        Intent intent = getIntent();

        gpsLatLng = new ArrayList<>();
        gpsLatLng.add(intent.getDoubleExtra("Latitude", 1.0));
        gpsLatLng.add(intent.getDoubleExtra("Longitude", 1.0));

        Toast.makeText(getApplicationContext(), gpsLatLng.get(0) + " " + gpsLatLng.get(1), Toast.LENGTH_SHORT).show();
        //double longitude = bundle.getDouble("Longitude");
        //double latitude = bundle.getDouble("Latitude");
    }

    /**
     * Preparing the map for the testing;
     *
     * @param googleMap
     *      Call the googleMap tool;
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Create initial map marker and zoom in on it

        final LatLng[] latLng = {new LatLng(gpsLatLng.get(0), gpsLatLng.get(1))};
        final MarkerOptions[] markerOptions = {new MarkerOptions()
                .position(latLng[0])
                .title("I'm here")};
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng[0]));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 16));
        googleMap.addMarker(markerOptions[0]);

        // Access database, retrieve QR codes and display them on the map
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                            for (Map<String, Object> code : codes) {
                                String codeContent = (String) code.get("content");
                                Double codeLat = 0.0;
                                Double codeLng = 0.0;
                                if (code.get("latitude") != null) {
                                    codeLat = (Double) (code.get("latitude"));
                                    codeLng = (Double) (code.get("longitude"));
                                }
                                latLng[0] = new LatLng(codeLat, codeLng);
                                // Create marker for the QR code
                                markerOptions[0] = new MarkerOptions()
                                        .position(latLng[0])
                                        .title("Nearby QR Code: " + codeContent);
                                googleMap.addMarker(markerOptions[0]);
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + codeContent);
                            }
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    // Callback:
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}