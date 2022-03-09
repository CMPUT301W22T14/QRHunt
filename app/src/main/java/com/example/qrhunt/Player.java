package com.example.qrhunt;

import com.google.zxing.qrcode.encoder.QRCode;

import java.util.ArrayList;

public class Player {
    /* Global Variables */
    private String uuid;

    private boolean isStoredRemotely = false;


    private String userName = null;
    private ArrayList<GameQRCode> QRCodeList = null;

    private String contactInfo = null;


    // Constructor
    Player (String uuid) {
        this.uuid = uuid;
    }



    Player (String uuid, String userName, ArrayList<GameQRCode> QRCodeList, String contactInfo) {
        this.uuid = uuid;
        this.userName = userName;
        this.QRCodeList = QRCodeList;


        this.contactInfo = contactInfo;
    }


    // Expand Methods:
    public int getMaxCodeScore() {
        return 0;
    }

    public int getMinCodeScore() {
        return 0;
    }

    public int getAvgCodeScore() {
        return 0;
    }

    public int getSumCodeScore() {
        return 0;
    }

    public int getTotalCodeNum() {
        return 0;
    }

    public void addQRCode(GameQRCode newQRCode) {
        QRCodeList.add(newQRCode);
    }



    // Getters
    public String getUserName() {
        return userName;
    }

    public String getUUID() {
        return uuid;
    }


    public boolean isStoredRemotely() {
        return isStoredRemotely;
    }



    public ArrayList<GameQRCode> getQRCodeList() {
        return QRCodeList;
    }

    public String getContactInfo() {
        return contactInfo;
    }


    //Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }


    public void setStoredRemotely(boolean storedRemotely) {
        isStoredRemotely = storedRemotely;
    }



    public void setQRCodeList(ArrayList<GameQRCode> QRCodeList) {
        this.QRCodeList = QRCodeList;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
