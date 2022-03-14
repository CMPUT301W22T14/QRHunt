
package com.example.qrhunt;

import android.graphics.Bitmap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 * This is a class represent the QR code
 */
public class GameQRCode {
    private String content;
    public int score;
    private final ArrayList<String> comments = new ArrayList<String>();
    private final ArrayList<String> uuidOfScanners = new ArrayList<String>();
    private Bitmap captureImage;


    public GameQRCode(String content) {
       this.content = content;
        calculateScore();
    }

    /**
     * This is to calculate the score of the QR code
     */
    private void calculateScore() {
        try {
            //from: stackoverflow.com
            //URL: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
            //Author: https://stackoverflow.com/users/22656/jon-skeet
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            String SHA256Hash = hexString.toString();
            score = hashToScore(SHA256Hash);
            // Then calculate the score
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is to calculate score according to the hash code
     * @param content
     *      Parameter is the QRCode content
     * @return
     *      Return the score according to the hash code
     */
    private int hashToScore(String content) {
        StringBuilder sb = new StringBuilder();
        int sum = 0;
        for (char c : content.toCharArray()) {
            if (sb.length() == 0)
                sb.append(c);
            else {
                if (sb.charAt(sb.length() - 1) == c)
                    sb.append(c);
                else {
                    if (sb.length() == 1) {
                        sb.setLength(0);
                        sb.append(c);
                    }
                    else {
                        int length = sb.length();
                        char prev = sb.charAt(length - 1);
                        //update sb
                        sb.setLength(0);
                        sb.append(c);
                        //calculate score
                        if (prev == '0')
                            sum += Math.round(Math.pow(20, length - 1));
                        else if (prev == 'a')
                            sum += Math.round(Math.pow(10, length - 1));
                        else if (prev == 'b')
                            sum += Math.round(Math.pow(11, length - 1));
                        else if (prev == 'c')
                            sum += Math.round(Math.pow(12, length - 1));
                        else if (prev == 'd')
                            sum += Math.round(Math.pow(13, length - 1));
                        else if (prev == 'e')
                            sum += Math.round(Math.pow(14, length - 1));
                        else if (prev == 'f')
                            sum += Math.round(Math.pow(15, length - 1));
                        else {
                            int number = prev - '0';
                            sum += Math.round(Math.pow(number, length - 1));
                        }
                    }
                }
            }
        }
        return sum;
    }

    /**
     * This gets the score of the QR code
     * @return
     *      Return the score of the QR code
     */
    public int getScore() {
        return score;
    }

    /**
     * This adds a new comment to the list of comments
     * @param inputComment
     *      The comment String for the QRCode
     */
    public void addComment(String inputComment) {
        comments.add(inputComment);
    }

    /**
     * This removes a comment from the list of comments
     * @param index
     *      The index of comment want to remove.
     */
    public void removeComment(int index) {
        comments.remove(index);
    }

    /**
     * This gets the list of all comments
     * @return
     *      Return the list of all comments
     */
    public ArrayList<String> showAllComments() {
        return comments;
    }

    /**
     * waiting to be done
     * @param uuidOfScanner
     *      The uuid get from the Scanner
     */
    public void addScanner(String uuidOfScanner) {
        uuidOfScanners.add(uuidOfScanner);
    }

    /**
     * waiting to be done
     * @param index
     *      The index of the Scanner waiting to be remove.
     */
    public void removeScanner(int index) {
        uuidOfScanners.remove(index);
    }

    /**
     * This shows all scanner.
     * @return
     *      The Sting[] of all scanners.
     */
    public ArrayList<String> showAllScanners() {
        return uuidOfScanners;
    }

    /**
     * This set the object/location's image;
     * @param captureImage
     *      The image captured.
     */
    public void setCaptureImage(Bitmap captureImage) {
        this.captureImage = captureImage;
    }

    /**
     * This gets the image of the object/location;
     * @return
     *      Return the image of the object/location
     */
    public Bitmap getCaptureImage() {
        return captureImage;
    }

    /**
     * This gets the content of the QR code;
     * @return
     *      Return the content of the QR code;
     */
    public String getContent() {
        return content;
    }

}
