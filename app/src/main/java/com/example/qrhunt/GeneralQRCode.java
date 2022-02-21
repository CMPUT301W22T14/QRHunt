package com.example.qrhunt;

public abstract class GeneralQRCode {

    protected String content;

    public GeneralQRCode(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public abstract void generateQRCode();
}
