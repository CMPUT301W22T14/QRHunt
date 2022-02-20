package com.example.qrhunt;

public class GameQRCode extends GeneralQRCode {
    private int score;


    public GameQRCode(String content) {
        super(content);
        calculateScore();
    }

    private void calculateScore() {
        int score = 10;
        this.score = score;
    }


    @Override
    public void generateQRCode() {

    }
}
