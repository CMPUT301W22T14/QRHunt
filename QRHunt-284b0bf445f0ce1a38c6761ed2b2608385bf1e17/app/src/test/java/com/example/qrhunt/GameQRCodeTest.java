package com.example.qrhunt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class GameQRCodeTest {
    private GameQRCode mockQRCode() {
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        return gameQRCode;
    }

    @Test
    void testCalculateScore() throws NoSuchAlgorithmException {
        GameQRCode gameQRCode = mockQRCode();

        assertEquals(19,  gameQRCode.getScore());
    }


    @Test
    void getContentTest() {
        GameQRCode gameQRCode = mockQRCode();
        assertEquals("BFG5DGW54",  gameQRCode.getContent());
    }

    @Test
    void addCommentTest() {
        GameQRCode gameQRCode = mockQRCode();
        gameQRCode.addComment("Hello World!!!");
        assertEquals(1,  gameQRCode.showAllComments().size());
    }

    @Test
    void showAllScannersTest() {
        GameQRCode gameQRCode = mockQRCode();
        gameQRCode.addComment("Hello World!!!");
        ArrayList<String> comments = gameQRCode.showAllComments();
        assertEquals("Hello World!!!", comments.get(0));
    }


}
