package com.example.qrhunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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


    // 创造函数：
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Players");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLatestPlayerCoordinate();

    }

    // player最新位置匹配函数：
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

                    //loadCodesCoordinates();
                }
            }
        });
    }


    // 权限获取函数：
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



    // Codes坐标加载函数：下载/去重/转换
    private void loadCodesCoordinates() {

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
                                Map<String, Double> map = (Map<String, Double>) (code.get("latestLatLng"));
                                if (map != null) {
                                    newCode.loadCoordinate(map.get("latitude"), map.get("longitude"));
                                    player.addQRCode(newCode);
                                }
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
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
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // Todo - 飞鱼：接入fdb，下载 全线player信息，接入集合转换；
        //FireDatabase fdb = new FireDatabase("Admin");
        //ArrayList<Player> players = fdb.getAllPlayersReload();



    }




    // Nearby判定函数：
    private boolean isCoordinateNearby(LatLng coordinate) {
        double threshold = 0.5;
        double playerLatitude = playerCoordinate.latitude;
        double playerLongitude = playerCoordinate.longitude;
        double codeLatitude = coordinate.latitude;
        double codeLongitude = coordinate.longitude;

        boolean isLatitudeClose = Math.abs(playerLatitude - codeLatitude) <= threshold;
        boolean isLongitudeClose = Math.abs(playerLongitude - codeLongitude) <= threshold;

        return isLatitudeClose && isLongitudeClose;
    }


    // 用户坐标展示函数：
    private Marker addPlayerMarker(GoogleMap googleMap) {
        Marker playerMarker = googleMap.addMarker(new MarkerOptions().position(playerCoordinate).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        return playerMarker;
    }


    // 近码坐标展示函数：
    private ArrayList<Marker> addCodeMarkers(GoogleMap googleMap) {
        ArrayList<Marker> codeMarkers = new ArrayList<>();
        int index = 1;
        for (LatLng coordinate : codesCoordinatesNearby) {
            Marker codeMarker = googleMap.addMarker(new MarkerOptions().position(coordinate).title( "Code" + index).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            codeMarkers.add(codeMarker);
            index += 1;
        }
        return codeMarkers;
    }


    // 地图响应函数：
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Marker playerMarker = addPlayerMarker(googleMap);
        //ArrayList<Marker> codeMarkers = addCodeMarkers(googleMap);
        //codeMarkers.add(playerMarker);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(playerMarker.getPosition(), 5));
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
                                Map<String, Double> map = (Map<String, Double>) (code.get("latestLatLng"));
                                if (map != null) {
                                    newCode.loadCoordinate(map.get("latitude"), map.get("longitude"));
                                    player.addQRCode(newCode);
                                }
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
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
                    ArrayList<Marker> codeMarkers = new ArrayList<>();
                    int index = 1;
                    for (LatLng coordinate : codesCoordinatesNearby) {
                        Marker codeMarker = googleMap.addMarker(new MarkerOptions().position(coordinate).title( "Code" + index).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        codeMarkers.add(codeMarker);
                        index += 1;
                    }
                    for (Marker marker : codeMarkers) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(codeMarkers.get(0).getPosition(), 8));
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(codeMarkers.get(0).getPosition(), 8));
    }


    // 休眠函数：（提升用户体验）
    private void timeSleep(int secondNum) {
        try {
            TimeUnit.SECONDS.sleep(secondNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}