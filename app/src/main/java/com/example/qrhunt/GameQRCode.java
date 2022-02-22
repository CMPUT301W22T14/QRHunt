package com.example.qrhunt;

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
