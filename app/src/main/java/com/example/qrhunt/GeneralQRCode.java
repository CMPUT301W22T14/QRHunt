package com.example.qrhunt;

public abstract class GeneralQRCode {
    protected String content = "123";

    public GeneralQRCode(String content) {
        this.content = content;
    }

    public String getContent() {
        return "123";
    }


    public abstract void generateQRCode();
}
