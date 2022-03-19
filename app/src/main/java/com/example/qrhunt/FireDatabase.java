package com.example.qrhunt;

import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireDatabase {
    private String uuid;
    private boolean dataExistedRemotely = false;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "Firestore";

    public FireDatabase(String uuid) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Players");
    }

    public void addOrUpdatePlayer(Player player) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("uuid", player.getUUID());
        data.put("username", player.getUserName());
        data.put("contactInfo", player.getContactInfo());
        List<GameQRCode> codes = player.getQRCodeList();
        data.put("codes", codes);
        collectionReference.document(player.getUUID()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    public void getSinglePlayerReload(Fishing fisher, Fragment frag, int type) {
        DocumentReference documentReference = collectionReference.document(uuid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        String uuidOutput = document.getId();
                        String contactInfoOutput = (String) document.get("contactInfo");
                        ArrayList<Map<String, Object>> codesOutput = (ArrayList<Map<String, Object>>) document.get("codes");
                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + uuidOutput);
                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + contactInfoOutput);
                        Player player = new Player(uuidOutput); // The player !!!!!!!!!!!!!!
                        player.setContactInfo(contactInfoOutput);
                        for (Map<String, Object> code : codesOutput) {
                            GameQRCode newCode = new GameQRCode((String) code.get("content"));
                            player.addQRCode(newCode);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                        }
                        // Callback:
                        switch (type) {
                            // Fisher Local Case:
                            case -2:
                                fisher.playerForLocal = player;
                                break;
                            // Fisher Input Case:
                            case -1:
                                fisher.choice = "INPUT";
                                fisher.playerForInput = player;
                                break;
                            // UsernameSearch Callback:
                            case 0:
                                fisher.playerForInput = player;
                                break;
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getAllPlayersReload() {
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
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + uuid);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + contactInfo);
                            Player player = new Player(uuid); // The player !!!!!!!!!!!!!!
                            player.setContactInfo(contactInfo);
                            for (Map<String, Object> code : codes) {
                                GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                player.addQRCode(newCode);
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                            }
                            players.add(player);
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

    public void removeCode(GameQRCode oldCode) {
        DocumentReference docRef = collectionReference.document(uuid);
        List<GameQRCode> newCodes = new ArrayList<>();

        DocumentReference documentReference = collectionReference.document(uuid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + uuid);
                        for (Map<String, Object> code : codes) {
                            GameQRCode newCode = new GameQRCode((String) code.get("content"));
                            if (!newCode.getContent().equals(oldCode.getContent()))
                                newCodes.add(newCode);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                        }
                        docRef.update("codes", newCodes);
                        // Callback:

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void isDatabaseExisted(Fishing fisher) {
        DocumentReference documentReference = collectionReference.document(uuid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    boolean result = false;
                    if (document.exists()) {
                        result = true;
                    }
                    // Callback:
                    if (fisher.choice.equals("LOCAL")) {
                        fisher.isFDBForLocalExisted = result;
                    }
                    else {
                        fisher.isFDBForInputExisted = result;
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
