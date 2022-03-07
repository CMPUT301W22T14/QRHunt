package com.example.qrhunt;

import com.google.zxing.qrcode.encoder.QRCode;

import java.util.ArrayList;

public class Player {
    /* Global Variables */
    private String userName;
    private String uuid;
    private boolean isStoredRemotely = false;
    private ArrayList<GameQRCode> QRCodeList= new ArrayList<GameQRCode>();
    private String contactInfo = null;


    // Constructor
    Player(String uuid) {
        this.uuid = uuid;
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
