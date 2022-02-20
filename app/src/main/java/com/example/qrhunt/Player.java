package com.example.qrhunt;

import java.util.ArrayList;

public class Player {
    private String userName;
    private String deviceID;
    PlayerQRCode playerQRCode;
    ArrayList<GameQRCode> QRCodeList= new ArrayList<GameQRCode>();

    Player(String deviceID){
        this.deviceID = deviceID;
    }

    String getUserName(){ return this.userName; }
    String getDeviceID(){ return this.deviceID; }
    PlayerQRCode getPlayerQRCode(){ return this.playerQRCode; }
    ArrayList<GameQRCode> getQRCodeList() { return this.QRCodeList; }
}
