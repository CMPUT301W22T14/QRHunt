package com.example.qrhunt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class PlayerTest {
    private Player mockPlayer() {
        Player player = new Player("12345");
        return player;
    }

    @Test
    void getUUIDTest() {
        Player player = mockPlayer();
        assertEquals("12345",  player.getUUID());
    }

    @Test
    void userNameTest() {
        Player player = mockPlayer();
        player.setUserName("Peter");
        assertEquals("Peter",  player.getUserName());
    }

    @Test
    void contactInformationTest() {
        Player player = mockPlayer();
        player.setContactInfo("1234567890");
        assertEquals("1234567890",  player.getContactInfo());
    }

    @Test
    void addQRCodeTest() {
        Player player = mockPlayer();
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        player.addQRCode(gameQRCode);
        assertEquals(1,  player.getQRCodeList().size());
        assertEquals("BFG5DGW54",  player.getQRCodeList().get(0).getContent());
    }

    @Test
    void getMinCodeScoreTest() {
        Player player = new Player("12345");
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        player.addQRCode(gameQRCode);
        assertEquals(19,  player.getMinCodeScore());
    }

    @Test
    void getMaxCodeScoreTest() {
        Player player = new Player("12345");
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        player.addQRCode(gameQRCode);
        assertEquals(19,  player.getMaxCodeScore());
    }

    @Test
    void getAvgCodeScoreTest() {
        Player player = new Player("12345");
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        player.addQRCode(gameQRCode);
        assertEquals(19.00,  player.getAvgCodeScore());
    }

    @Test
    void getSumCodeScoreTest() {
        Player player = new Player("12345");
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        player.addQRCode(gameQRCode);
        assertEquals(19,  player.getSumCodeScore());
    }

    @Test
    void getTotalCodeNumTest() {
        Player player = new Player("12345");
        GameQRCode gameQRCode = new GameQRCode("BFG5DGW54");
        player.addQRCode(gameQRCode);
        assertEquals(1,  player.getTotalCodeNum());
    }
}
