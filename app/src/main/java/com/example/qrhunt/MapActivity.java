package com.example.qrhunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import java.util.concurrent.TimeUnit;


/**
 * The class is for control the map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    LatLng playerCoordinate = new LatLng(0, 0);
    ArrayList<LatLng> codesCoordinatesNearby = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    String TAG = "Players";


    /**
     * The creation method
     * @param savedInstanceState
     *      This is an object type of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Players");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLatestPlayerCoordinate();

    }


    /**
     * Get the location of the player
     */
    private void fetchLatestPlayerCoordinate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    playerCoordinate = new LatLng(location.getLatitude(), location.getLongitude());
                    Toast.makeText(getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }


    /**
     * Request permission
     * @param requestCode
     *      This is an object type of int
     * @param permissions
     *      This is an object type of String[]
     * @param grantResults
     *      This is an object type of int[]
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLatestPlayerCoordinate();
                }
                break;
        }
    }


    /**
     * Check if this code is near to the player
     * @param coordinate
     *      The location of the code
     * @return
     *      If the code is near to the player
     */
    private boolean isCoordinateNearby(LatLng coordinate) {
        double threshold = 1;
        double playerLatitude = playerCoordinate.latitude;
        double playerLongitude = playerCoordinate.longitude;
        double codeLatitude = coordinate.latitude;
        double codeLongitude = coordinate.longitude;

        boolean isLatitudeClose = Math.abs(playerLatitude - codeLatitude) <= threshold;
        boolean isLongitudeClose = Math.abs(playerLongitude - codeLongitude) <= threshold;

        return isLatitudeClose && isLongitudeClose;
    }


    /**
     * add player markers
     * @param googleMap
     *      This is an object type of GoogleMap
     * @return
     *      Returns the markers
     */
    private Marker addPlayerMarker(GoogleMap googleMap) {
        Marker playerMarker = googleMap.addMarker(new MarkerOptions().position(playerCoordinate).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        return playerMarker;
    }


    /**
     * This is a method that deals with map
     * @param googleMap
     *      This is an object type of GoogleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Marker playerMarker = addPlayerMarker(googleMap);
        //ArrayList<Marker> codeMarkers = addCodeMarkers(googleMap);
        //codeMarkers.add(playerMarker);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(playerMarker.getPosition(), 15));
        timeSleep(1);

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Player> players = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String uuid = document.getId();
                            String contactInfo = (String) document.get("contactInfo");
                            ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                            Player player = new Player(uuid); // The player !!!!!!!!!!!!!!
                            player.setContactInfo(contactInfo);
                            for (Map<String, Object> code : codes) {
                                GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                if (code.get("latitude") != null || code.get("longitude") != null) {
                                    newCode.loadCoordinate((Double) code.get("latitude"), (Double) code.get("longitude"));
                                    player.addQRCode(newCode);
                                }
                            }
                            players.add(player);
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    for (Player player : players) {
                        ArrayList<GameQRCode> codesOfPlayer = player.getQRCodeList();
                        for (GameQRCode code : codesOfPlayer) {
                            LatLng codeCoordinate = new LatLng(code.getLatitude(), code.getLongitude());

                            if (isCoordinateNearby(codeCoordinate)) {
                                codesCoordinatesNearby.add(codeCoordinate);
                            }
                        }
                    }

                    int index = 1;
                    Log.d(TAG,  "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@!!!!!!!!!!!!!!!!!!!!!!!!!!"+codesCoordinatesNearby.size());
                    for (LatLng coordinate : codesCoordinatesNearby) {
                        Marker codeMarker = googleMap.addMarker(new MarkerOptions().position(coordinate).title( "Code" + index).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        index += 1;
                    }

                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    /**
     * Sleep method
     * @param secondNum
     *      The time of sleep
     */
    private void timeSleep(int secondNum) {
        try {
            TimeUnit.SECONDS.sleep(secondNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}