package com.example.qrhunt;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private CollectionReference collectionReferenceForCodes;
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
        collectionReferenceForCodes = db.collection("Codes");
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

    public void addComment(GameQRCode code) {
        HashMap<String, Object> data = new HashMap<>();

    }


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

    /**
     * This method gets the information of a single player
     * @param callback
     *      This is an object type of PlayerCallback
     */
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
                                GameQRCode oldCode = new GameQRCode((String) code.get("content"));
                                Map<String, Double> map = (Map<String, Double>) (code.get("latestLatLng"));
                                if (map != null) {
                                    oldCode.loadCoordinate(map.get("latitude"), map.get("longitude"));
                                }
                                newCodes.add(oldCode);
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + oldCode.getContent());
                            }
                        }
                        //Bitmap image =  newCode.getCaptureImage();
                        //newCode.setCaptureImage(null);
                        newCodes.add(newCode);
                        documentReference.update("codes", newCodes);
                        //newCode.setCaptureImage(image);
                        //Call fragment
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        HashMap<String, Object> data = new HashMap<>();
        List<String> comments = new ArrayList<>();
        data.put("comments", comments);
        collectionReferenceForCodes.document(newCode.getHash()+"\n"+uuid).set(data)
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
