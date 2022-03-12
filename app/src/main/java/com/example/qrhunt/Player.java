package com.example.qrhunt;

import com.google.zxing.qrcode.encoder.QRCode;


import java.text.DecimalFormat;
import java.util.ArrayList;


public class Player {
    /* Global Variables */
    private String uuid;
    private boolean isStoredRemotely = false;
    private String userName = null;
    private ArrayList<GameQRCode> QRCodeList = new ArrayList<GameQRCode>();
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
        if (QRCodeList.size() == 0)
            return 0;

        int max = Integer.MIN_VALUE;
        for (GameQRCode code : QRCodeList) {
            max = Math.max(max, code.getScore());
        }
        return max;
    }

    public int getMinCodeScore() {
        if (QRCodeList.size() == 0)
            return 0;

        int min = Integer.MAX_VALUE;
        for (GameQRCode code : QRCodeList) {
            min = Math.min(min, code.getScore());
        }
        return min;
    }

    public double getAvgCodeScore() {
        if (QRCodeList.size() == 0)
            return 0;

        double sum = 0;
        for (GameQRCode code : QRCodeList) {
            sum += code.getScore();
        }
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        double avg = Double.parseDouble(df.format(sum / QRCodeList.size()));
        return avg;
    }

    public int getSumCodeScore() {
        int sum = 0;
        for (GameQRCode code : QRCodeList) {
            sum += code.getScore();
        }
        return sum;
    }

    public int getTotalCodeNum() {
        return QRCodeList.size();
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

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
