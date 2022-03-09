package com.example.qrhunt;

import android.graphics.Bitmap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class GameQRCode {
    private String content;
    private int score;
    private final ArrayList<String> comments = new ArrayList<String>();
    private final ArrayList<String> uuidOfScanners = new ArrayList<String>();
    private Bitmap captureImage;


    public GameQRCode(String content) {
       this.content = content;
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
            score = hashToScore(SHA256Hash);
            // Then calculate the score
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


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

    public void setCaptureImage(Bitmap captureImage) {
        this.captureImage = captureImage;
    }

    public Bitmap getCaptureImage() {
        return captureImage;
    }

    public String getContent() {
        return content;
    }

    public void generateQRCode() {

    }
}
