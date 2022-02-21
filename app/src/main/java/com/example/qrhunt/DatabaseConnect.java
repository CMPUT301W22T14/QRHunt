package com.example.qrhunt;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class DatabaseConnect {
    private String uuid;


    public DatabaseConnect(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean isDatabaseExisted() {
        //..
        return false;
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
        return new Player("000");
        //...
    }

    public void addNew(Player player) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Players").child(player.getUUID()).setValue(player);
    }
}
