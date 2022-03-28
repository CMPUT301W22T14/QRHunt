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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


/**
 * The class is for control the map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    ArrayList<Double> gpsLatLng;
    ArrayList<GameQRCode> QRCodes;
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

        //fetchLastLocation();
        Intent intent = getIntent();

        gpsLatLng = new ArrayList<>();
        gpsLatLng.add(intent.getDoubleExtra("Latitude", 1.0));
        gpsLatLng.add(intent.getDoubleExtra("Longitude", 1.0));

        QRCodes = new ArrayList<>();
        getQRCodes();

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

        LatLng latLng = new LatLng(gpsLatLng.get(0), gpsLatLng.get(1));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("I'm here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);

        for (int i = 0; i < QRCodes.size(); i++) {
            latLng = new LatLng(QRCodes.get(i).getLatitude(), QRCodes.get(i).getLongitude());
            markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Nearby QR Code");
            googleMap.addMarker(markerOptions);
        }
        // Just for verifying if multiple markers can be displayed this way
        for (int i = 0; i < 10; i++) {
            latLng = new LatLng(gpsLatLng.get(0) + i, gpsLatLng.get(1) + i);
            markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Nearby QR Code");
            googleMap.addMarker(markerOptions);
        }

    }

    public void getQRCodes() {
        //
    }
}