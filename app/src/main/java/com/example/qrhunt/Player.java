package com.example.qrhunt;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


/**
 * This is a class that represents a player
 */
public class Player implements Serializable {
    /* Global Variables */
    private String uuid;
    private boolean isStoredRemotely = false;
    private String userName = null;
    private ArrayList<GameQRCode> QRCodeList = new ArrayList<GameQRCode>();
    private String contactInfo = null;


    /**
     * This constructs the Player with uuid as its uuid and userName.
     * @param uuid
     *      This is the only primary key and unique String for each player.
     */
    // Constructor
    Player (String uuid) {
        this.uuid = uuid;
        this.userName = uuid;
    }

    Player (String uuid, String userName, ArrayList<GameQRCode> QRCodeList, String contactInfo) {
        this.uuid = uuid;
        this.userName = userName;
        this.QRCodeList = QRCodeList;
        this.contactInfo = contactInfo;
    }


    // Expand Methods:

    /**
     * This gets the max code score of the player
     * @return
     *      Return the max score of the player
     */
    public int getMaxCodeScore() {
        if (QRCodeList.size() == 0)
            return 0;

        int max = Integer.MIN_VALUE;
        for (GameQRCode code : QRCodeList) {
            max = Math.max(max, code.getScore());
        }
        return max;
    }

    /**
     * This gets the min code score of the player
     * @return
     *      Return the min score of the player
     */
    public int getMinCodeScore() {
        if (QRCodeList.size() == 0)
            return 0;

        int min = Integer.MAX_VALUE;
        for (GameQRCode code : QRCodeList) {
            min = Math.min(min, code.getScore());
        }
        return min;
    }

    /**
     * This gets the average code score of the player
     * @return
     *      Return the average score of the player
     */
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

    /**
     * This gets the total code score of the player
     * @return
     *      Return the total score of the player
     */
    public int getSumCodeScore() {
        int sum = 0;
        for (GameQRCode code : QRCodeList) {
            sum += code.getScore();
        }
        return sum;
    }

    /**
     * This gets the total number of codes the player owns
     * @return
     *      Return the total number of codes the player owns
     */
    public int getTotalCodeNum() {
        return QRCodeList.size();
    }

    /**
     * This add a new QR code to the list
     * @param newQRCode
     *      This is the new GameQRCode that need to be added into the list.
     */
    public void addQRCode(GameQRCode newQRCode) {
        QRCodeList.add(newQRCode);
    }


    // Getters

    /**
     * Get the username of the player
     * @return
     *      Return the username of the player
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get the UUID of the player
     * @return
     *       Return the UUID of the player
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Get the list of the QR codes that the player owns
     * @return
     *      A list of the QR codes that the player owns
     */
    public ArrayList<GameQRCode> getQRCodeList() {
        return QRCodeList;
    }

    /**
     * Get the contact information of the player
     * @return
     *      Return the contact information of the player
     */
    public String getContactInfo() {
        return contactInfo;
    }


    /* Setters */

    /**
     * Change the username of the player
     * @param userName
     *      This is the String of the player's userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Set the UUID of the player
     * @param uuid
     *      This is the String of the player's uuid
     */
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Change the contact informtion of the player
     * @param contactInfo
     *      This is the String of player's contact info.
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return uuid.equals(player.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public boolean hasThisCode(GameQRCode code) {
        for (GameQRCode c : QRCodeList) {
            if (c.getHash().equals(code.getHash()))
                return true;
        }
        return false;
    }
}
