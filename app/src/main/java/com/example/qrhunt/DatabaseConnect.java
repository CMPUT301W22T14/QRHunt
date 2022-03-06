package com.example.qrhunt;

import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DatabaseConnect {
    private String uuid;
    private Player player = null;
    private boolean dataExistedRemotely = false;


    public DatabaseConnect(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return player;
    }

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
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return dataExistedRemotely;
    }

    public ArrayList<GameQRCode> getGameCodesReload() {
        // success/fail --> return true/false
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Player player;
        mDatabase.child("players").child(uuid).getKey();
        //...
        return new ArrayList<GameQRCode>();
    }

    public Player getPlayerReload() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Player");
        Query checkPlayer = reference.orderByChild("username").equalTo(uuid);
        checkPlayer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child(uuid).child("userName").getValue(String.class);
                ArrayList<GameQRCode> QRCodeList = snapshot.child(uuid).child("QRCodeList").getValue(ArrayList<?>.class);
                String contactInfo = snapshot.child(uuid).child("contactInfo").getValue(String.class);
                player = new Player(uuid, userName, QRCodeList, contactInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return player;
    }

    public void addNew(Player player) {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();  //This is a call to the top level, which is the root node
        DatabaseReference referencePlayer = rootNode.getReference("Players");
        referencePlayer.child(uuid).setValue(player);
    }

    public boolean removeCode(int codeIndex) {
        //..
        return false;
    }
}
