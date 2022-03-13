package com.example.qrhunt;

import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.ArrayList;

/**
 * This is a class connects the realtime database with the app.
 */
public class DatabaseConnect {
    private String uuid;
    private Player player = null;
    private boolean dataExistedRemotely = false;


    public DatabaseConnect(String uuid) {
        this.uuid = uuid;
    }

    /**
     * This method gets the UUID of the current DatabaseConnect.
     * @return
     *      Returns the uuid of the DatabaseConnect
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * This method gets the Player of the current DatabaseConnect.
     * @return
     *      Returns the Player of the DatabaseConnect
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * This method checks if the DatabaseConnect exist.
     * @return
     *      Returns the boolean of whether database exists remotely.
     */
    public boolean isDatabaseExisted() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Player");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uuid)) {
                dataExistedRemotely = true;
                }
                dataExistedRemotely = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Temporailly skipped, will implement if error catched in testing
            }
        });
        return dataExistedRemotely;
    }

    /**
     * This method reloads all the GameQRCode of the current DatabaseConnect.
     * @return
     *      Returns the GameQRCode[].
     */
    public ArrayList<GameQRCode> getGameCodesReload() {
        // success/fail --> return true/false
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Player");
        Player player;
        ArrayList<String> codeContents = new ArrayList<String>();
        //Assumption: given an uuid, we get all the QrCode the id has scanned before
        mDatabase.child(uuid).child("QRCode").child("strings").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //The following loop will get all the values
                    for (DataSnapshot child :task.getResult().getChildren()){
                        codeContents.add(child.getKey());
                    }
            }
            }
        });
        //now we create QrCodes using its content previously stored here
        ArrayList<GameQRCode> gameQRCodes = new ArrayList<>();
        int i =0;
        while ( i < codeContents.size()){
            GameQRCode code = new GameQRCode(codeContents.get(i));
            gameQRCodes.add(code);
        }
        return gameQRCodes;
    }

    /**
     * This method reloads the Player of the current DatabaseConnect.
     * @return
     *      Returns the Player of the DatabaseConnect
     */
    public Player getPlayerReload() {
        // Since we are returning player here, I would assume we are returning a single player?
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Player");
        Query checkPlayer = reference.orderByChild("username").equalTo(uuid);
        checkPlayer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child(uuid).child("userName").getValue(String.class);
                ArrayList<GameQRCode> QRCodeList = getGameCodesReload();
                String contactInfo = snapshot.child(uuid).child("contactInfo").getValue(String.class);
                player = new Player(uuid, userName, QRCodeList, contactInfo);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return player;
    }

    /**
     * This method add a new Player to the remote database.
     * @param player
     */
    public void addNew(Player player) {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();  //This is a call to the top level, which is the root node
        DatabaseReference referencePlayer = rootNode.getReference("Players");
        //Instead of setting new players directly, we have to set its values separately because
        // QRCode class is hard to maintain and have to be stored using only its content
        referencePlayer.child(uuid).child("uuid").setValue(player.getUUID());
        referencePlayer.child(uuid).child("username").setValue(player.getUserName());
        referencePlayer.child(uuid).child("contactInfo").setValue(player.getContactInfo());
        ArrayList<String> codes = new ArrayList<>();
        int i = 0;
        //stored the contents of QRCode only. consequently, I added the reconstructing QRCodeList
        //feature when we are reading from the database
        while (i< player.getQRCodeList().size()){
            codes.add(player.getQRCodeList().get(i).getContent());
            i+=1;
        }
        referencePlayer.child(uuid).child("QRCode").setValue(codes);
    }

    /**
     * This method removes the selected GameQRCode from current Database Connect.
     * @param codeAtPos
     * @return
     *      Return the result of if remove success.
     */
    public boolean removeCode(GameQRCode codeAtPos) {
        // Will remove the code at with the QR supplied. 
        String StringCode = codeAtPos.getContent();
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();  //This is a call to the top level, which is the root node
        DatabaseReference referencePlayer = rootNode.getReference("Players");
        referencePlayer.child(uuid).child("QRCode").child(StringCode).removeValue();
        return false;
    }
}
