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


// Todo - 01 - Removing Code:
//          --> (*Sometimes) App Closing, Removing Successfully;
//          --> (Sometimes) Displaying Problem, Removing Successfully;
// Todo - 02 - Double Codes Adding:
//          --> (*Always) App Closing, Adding Successfully;
// Todo - 03 - Code Picture Added Cannot Display;
// Todo - 04 - General Code Adding:
//          --> (*Sometimes) One More Blank Item Appeared in The listView Above The New Adding One (Sometimes), Adding Successfully;
//          --> (Always) Comments Cannot Be Added Successfully;



/**
 * This class is in charge of connecting/writing and reading from database
 * */
public class FireDatabase {
    private String uuid;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "Firestore";

    /**
     *Constructor method.
     * This constructor method will assign the parameter that is passed into this function
     * to private variables for usage.
     * @param uuidInput
     *      This is a string representing the identity of the player.
     * */
    public FireDatabase(String uuidInput) {
        db = FirebaseFirestore.getInstance();
        uuid = uuidInput;
        collectionReference = db.collection("Players");
    }

    /**
     * This method will update our database when we need to add players to the databse or
     * updated a specific information in the database.
     * @param player
     *      This is a Player object that needs to be stored or updated from cloud
     * */
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

    /**
     * This method will read the data about a player from the database. and recreate the player object
     * @param obj
     *      This is a object typed parameter that will be turn into our types of variable in this function
     * @param type
     *      This is an integer indicating few situations that will be handled differently in the function
     * @param callback
     *      This is the callback for getting the reload player
     * */
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
                        leaderboard.setPlayer(uuid);
                        break;
                }
            }
        });
    }
    public void getSinglePlayerReload(PlayerCallback callback) {
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
                        if (codesOutput != null) {
                            for (Map<String, Object> code : codesOutput) {
                                GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                playerTarget.addQRCode(newCode);
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                            }
                        }
                    }
                }
                callback.callBack(playerTarget);
            }
        });
    }

    /**
     * This method will read the data about all player from the database. and recreate the Player object.
     * This is pretty much an extension from the getSinglePlayerReload() function. We use two functions here
     * mainly for clearity and easier debugging access.
     * */
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

    /**
     * This method will add the new QRCode the player get to the database.
     * @param newCode
     *      This is a the new QR Code that need to be added in GameQRCode type.
     */
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
                        if (codes != null) {
                            for (Map<String, Object> code : codes) {
                                GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                newCodes.add(newCode);
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                            }
                        }

                        newCodes.add(newCode);
                        documentReference.update("codes", newCodes);
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


    /**
     * This method will remove the Qrcode from the database or do nothing if the QrCode does not exist
     * in the database
     * @param oldCode
     *      * This parameter is the qrCode object that we want to remove from the cloud
     * */
    public void removeCode(GameQRCode oldCode) {
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
                            if (!newCode.getHash().equals(oldCode.getHash()))
                                newCodes.add(newCode);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                        }
                        documentReference.update("codes", newCodes);
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
