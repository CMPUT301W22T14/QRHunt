package com.example.qrhunt;

import java.util.ArrayList;


public class Player {
    private String userName;
    private String uuid;
    PlayerQRCode loginQRCode;
    PlayerQRCode profileQRCode;
    ArrayList<GameQRCode> QRCodeList= new ArrayList<GameQRCode>();

    Player(String uuid) {
        this.uuid = uuid;
    }


    String getUserName() {
        return this.userName;
    }
    String getUUID() {
        return this.uuid;
    }
    PlayerQRCode getLoginQRCode() {
        return this.loginQRCode;
    }
    PlayerQRCode getProfileQRCode() {
        return this.profileQRCode;
    }
    ArrayList<GameQRCode> getQRCodeList() {
        return this.QRCodeList;
    }


}
