package com.example.qrhunt;

import java.util.ArrayList;

public class Player {
    private String userName;
    private String uuid;
    private boolean isStoredRemotely = false;
    private PlayerQRCode loginQRCode;
    private PlayerQRCode profileQRCode;
    private ArrayList<GameQRCode> QRCodeList= new ArrayList<GameQRCode>();

    Player(String uuid) {
        this.uuid = uuid;
    }


    public ArrayList<GameQRCode> getQRCodeList() {
        return QRCodeList;
    }

    public int getQRCodeListSum() {
        return QRCodeList.size();
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

}
