package com.example.qrhunt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireDatabase {
    private String uuid;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "Firestore";


    public FireDatabase(String uuidInput) {
        db = FirebaseFirestore.getInstance();
        uuid = uuidInput;
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

    /*
    public void getSinglePlayerReload(Object obj, int type) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot document: queryDocumentSnapshots) {
                    if (document.getId().equals("Player 1")) {
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
                            // 0 --> CustomList
                            case 0:
                                CustomList cl = (CustomList) obj;
                                cl.setPlayer(player);
                                break;
                            // 1 -->
                            case 1:
                                break;
                            // 2 --> ProfileDisplayFragment
                            case 2:
                                ProfileDisplayFragment profile = (ProfileDisplayFragment) obj;
                                profile.setPlayer(player);
                                break;
                            // 3 --> LeaderBoardFragment
                            case 3:
                                LeaderBoardFragment leaderboard = (LeaderBoardFragment) obj;
                                leaderboard.setPlayer(player);
                                break;
                        }

                    }
                }
            }
        });
    }


    public void getSinglePlayerReload(Object obj, int type) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                players.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    //Log.d(TAG, String.valueOf(doc.getId()));
                    String uuid = doc.getId();
                    String contactInfo = (String) doc.getData().get("contactInfo");
                    //List<GameQRCode> codes = (List<GameQRCode>) doc.getData().get("codes");
                    Player player = new Player(uuid);
                    player.setContactInfo(contactInfo);
                    //if (codes != null) {
                    //    for (GameQRCode code : codes)
                    //        player.addQRCode(code);
                    //}
                    players.add(player);
                }
            }
        });
    }*/

    public void getSinglePlayerReload(Object obj, int type, Callback callback) {
        Map<String, Object> player = new HashMap<>();
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                Player playerTarget = new Player("NOT EXIST");
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    //Log.d(TAG, String.valueOf(doc.getId()));
                    String uuidGet = doc.getId();
                    Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++" + uuidGet + "\n");
                    if (uuid.equals(uuidGet)){
                        String contactInfo = (String) doc.getData().get("contactInfo");
                        playerTarget = new Player(uuidGet);
                        playerTarget.setContactInfo(contactInfo);
                        ArrayList<Map<String, Object>> codesOutput = (ArrayList<Map<String, Object>>) (doc.get("codes"));
                        for (Map<String, Object> code : codesOutput) {
                            GameQRCode newCode = new GameQRCode((String) code.get("content"));
                            playerTarget.addQRCode(newCode);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                        }
                    }
                }
                // Callback:
                switch (type) {
                    // 0 --> CustomList
                    case 0:
                        player.put("playerReloaded", playerTarget);
                        callback.feedback(player);
                        break;
                    // 1 -->
                    case 1:
                        break;
                    // 2 --> ProfileDisplayFragment
                    case 2:
                        ProfileDisplayFragment profile = (ProfileDisplayFragment) obj;
                        profile.setPlayer(playerTarget);
                        break;
                    // 3 --> LeaderBoardFragment
                    case 3:
                        LeaderBoardFragment leaderboard = (LeaderBoardFragment) obj;
                        leaderboard.setPlayer(playerTarget);
                        break;
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

    public void addNewQRCode(GameQRCode newCode) {
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
                            newCodes.add(newCode);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                        }
                        newCodes.add(newCode);
                        docRef.update("codes", newCodes);
                        //Call fragment
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
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


}
