package com.example.qrhunt;

import java.util.ArrayList;

public class Player {
    private String userName;
    private String deviceID;
    PlayerQRCode loginQRCode;
    PlayerQRCode profileQRCode;
    ArrayList<GameQRCode> QRCodeList= new ArrayList<GameQRCode>();

    Player(String deviceID){
        this.deviceID = deviceID;
    }

    String getUserName(){ return this.userName; }
    String getDeviceID(){ return this.deviceID; }
    PlayerQRCode getLoginQRCode(){ return this.loginQRCode; }
    PlayerQRCode getProfileQRCode(){ return this.profileQRCode; }
    ArrayList<GameQRCode> getQRCodeList() { return this.QRCodeList; }
}
