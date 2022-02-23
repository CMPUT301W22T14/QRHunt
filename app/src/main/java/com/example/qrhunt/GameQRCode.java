package com.example.qrhunt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class GameQRCode extends GeneralQRCode {
    private int score;
    private final ArrayList<String> comments = new ArrayList<String>();
    private final ArrayList<String> uuidOfScanners = new ArrayList<String>();



    public GameQRCode(String content) {
        super(content);
        calculateScore();
    }

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
            // Then calculate the score
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int score = 10;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void addComment(String inputComment) {
        comments.add(inputComment);
    }

    public void removeComment(int index) {
        comments.remove(index);
    }

    public ArrayList<String> showAllComments() {
        return comments;
    }

    public void addScanner(String uuidOfScanner) {
        uuidOfScanners.add(uuidOfScanner);
    }

    public void removeScanner(int index) {
        uuidOfScanners.remove(index);
    }

    public ArrayList<String> showAllScanners() {
        return uuidOfScanners;
    }

    @Override
    public void generateQRCode() {

    }
}
