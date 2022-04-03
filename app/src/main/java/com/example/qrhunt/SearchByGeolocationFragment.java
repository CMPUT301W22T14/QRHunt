package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SearchByGeolocationFragment extends DialogFragment {
    private EditText latitudeText;
    private EditText longitudeText;
    private Button button;
    private ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Players");
    private String TAG = "players";


    public SearchByGeolocationFragment() {
        // Required empty public constructor
    }

    /**
     * Called by the android system to build up the fragment;
     *
     * @param savedInstanceState
     *      Last state record;
     */
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Locations Marked：
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search_by_geolocation, null);

        latitudeText = view.findViewById(R.id.latitude_textview);
        longitudeText = view.findViewById(R.id.longitude_textView);
        button = view.findViewById(R.id.confirm_search_nearby_button);
        listView = view.findViewById(R.id.nearby_location_codes_listview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!latitudeText.getText().toString().equals("") && !longitudeText.getText().toString().equals("")) {
                    double la = Double.parseDouble(latitudeText.getText().toString());
                    double lon = Double.parseDouble(longitudeText.getText().toString());
                    LatLng latLng = new LatLng(la, lon);
                    fillTheListView(latLng);
                    latitudeText.setText("");
                    longitudeText.setText("");
                }
            }
        });



        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Search Codes By Geolocation").create();

    }

    public void fillTheListView(LatLng enteredLocation) {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Player> players = new ArrayList<>();
                    List<String> resultCodes = new ArrayList<>();
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

                            if (isCoordinateNearby(enteredLocation, codeCoordinate)) {
                                resultCodes.add(code.getHash());
                            }
                        }
                    }
                    ArrayAdapter<String> codesAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, resultCodes);
                    listView.setAdapter(codesAdapter);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private boolean isCoordinateNearby(LatLng enteredLocation, LatLng codeLocation) {
        double threshold = 0.5;
        double enteredLatitude = enteredLocation.latitude;
        double enteredLongitude = enteredLocation.longitude;
        double codeLatitude = codeLocation.latitude;
        double codeLongitude = codeLocation.longitude;

        boolean isLatitudeClose = Math.abs(enteredLatitude - codeLatitude) <= threshold;
        boolean isLongitudeClose = Math.abs(enteredLongitude - codeLongitude) <= threshold;

        return isLatitudeClose && isLongitudeClose;
    }


}