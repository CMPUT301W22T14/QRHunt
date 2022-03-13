package com.example.qrhunt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;


// Current DataFrame Tests Cover All Methods of:
// - [DatabaseConnect]
// - [Player]
// - [GameQRCode] (more tests needed)

// - Todo: Adding More Tests for [GameQRCode], Fixing ActivityMain Foreign Channel (uuid returned from scanner) - for Yufei
// - Todo: Passing All The Tests Listed Below, Fixing DB Parts AND Reporting Bugs in Other Parts - for Yujie



public class DataFrameTEST {
    DatabaseConnect dbc_localUUID;
    DatabaseConnect dbc_fakeUUID_1;
    DatabaseConnect dbc_fakeUUID_2;
    DatabaseConnect dbc_fakeUUID_3;
    GameQRCode code7, code8;
    Player localPlayer, fakePlayer_3;


    @Test
    public void databaseInputTest(){
        
        // Players Setting (test PlayerConstructor/getUUID/UUID.randomUUID()/setContactInfo/setUserName)
        String localUUID = UUID.randomUUID().toString();
        localPlayer = new Player(localUUID);
        localPlayer.setContactInfo("587-379-6201");
        localPlayer.setUserName("Fucking301");
        String fakeUUID_1 = "11aa4468-a1d5-11ec-b909-0242ac120002";
        Player fakePlayer_1 = new Player(fakeUUID_1);
        String fakeUUID_2 = "26d43baa-a1d5-11ec-b909-0242ac120002";
        Player fakePlayer_2 = new Player(fakeUUID_2);
        String fakeUUID_3 = "31c8268e-a1d5-11ec-b909-0242ac120002";
        fakePlayer_3 = new Player(fakeUUID_3);
        assertSame("11aa4468-a1d5-11ec-b909-0242ac120002", fakePlayer_1.getUUID());

        // Codes Setting (test score/comment/removeComment/showAllComments/getScore)
        GameQRCode code1 = new GameQRCode("content for code1");
        code1.addComment("comment_1 for code1");
        code1.addComment("comment_2 for code1");
        code1.addComment("comment_3 for code1");
        code1.addComment("comment_4 for code1");
        code1.addComment("comment_5 for code1");
        code1.addComment("comment_6 for code1");
        code1.addComment("comment_7 for code1");
        code1.addComment("comment_8 for code1");
        code1.addComment("comment_9 for code1");
        code1.score = 100;
        code1.removeComment(8);
        code1.removeComment(3);
        assertSame(100, code1.getScore());
        assertSame(7, code1.showAllComments().size());
        GameQRCode code2 = new GameQRCode("content for code2");
        code1.addComment("comment for localMachinePlayer_code2");
        code2.score = -101;
        GameQRCode code3 = new GameQRCode("content for code3");
        GameQRCode code4 = new GameQRCode("content for code4");
        GameQRCode code5 = new GameQRCode("content for code5");
        GameQRCode code6 = new GameQRCode("content for code6");
        code6.score = 2;
        code7 = new GameQRCode("content for code7");
        code8 = new GameQRCode("content for code8");

        // AddingCodeToPlayer (test addQRCode)
        localPlayer.addQRCode(code1);
        localPlayer.addQRCode(code2);
        localPlayer.addQRCode(code3);
        localPlayer.addQRCode(code4);
        localPlayer.addQRCode(code5);
        localPlayer.addQRCode(code6);
        localPlayer.addQRCode(code7);
        localPlayer.addQRCode(code8);
        fakePlayer_1.addQRCode(code1);
        fakePlayer_1.addQRCode(code2);
        fakePlayer_1.addQRCode(code3);
        fakePlayer_1.addQRCode(code4);
        fakePlayer_2.addQRCode(code5);
        fakePlayer_2.addQRCode(code6);
        fakePlayer_2.addQRCode(code7);
        fakePlayer_2.addQRCode(code8);
        assertSame(8, localPlayer.getQRCodeList().size());
        assertSame(4, fakePlayer_2.getQRCodeList().size());
        assertSame(0, fakePlayer_3.getQRCodeList().size());


        // DBC Creating (test dbcConstructor)
        dbc_localUUID = new DatabaseConnect(localUUID);
        dbc_fakeUUID_1 = new DatabaseConnect(fakeUUID_1);
        dbc_fakeUUID_2 = new DatabaseConnect(fakeUUID_2);
        dbc_fakeUUID_3 = new DatabaseConnect(fakeUUID_3);

        // DBC to BC (test addNew)
        dbc_localUUID.addNew(localPlayer);
        dbc_fakeUUID_1.addNew(fakePlayer_1);
        dbc_fakeUUID_2.addNew(fakePlayer_2);
        dbc_fakeUUID_3.addNew(fakePlayer_3);
    }

    @Test
    public void databaseOutputTest(){
        // DB Existing (test isDatabaseExisted)
        assertTrue(dbc_localUUID.isDatabaseExisted());
        assertTrue(dbc_fakeUUID_1.isDatabaseExisted());
        assertTrue(dbc_fakeUUID_2.isDatabaseExisted());
        assertTrue(dbc_fakeUUID_3.isDatabaseExisted());

        // DB Editing (test removeCode)
        dbc_fakeUUID_2.removeCode(code7);
        dbc_fakeUUID_2.removeCode(code8);


        // Players Rebuild (test getUserName/getPlayer/showAllComments/getQRCodeList/getScore/DB Structure Keeping/getContactInfo/getSumCodeScore/getMaxCodeScore/getMinCodeScore/getAvgCodeScore/getTotalCodeNum)
        Player localPlayer = dbc_localUUID.getPlayer();
        Player fakePlayer_1 = dbc_fakeUUID_1.getPlayer();
        Player fakePlayer_2 = dbc_fakeUUID_2.getPlayer();
        Player fakePlayer_3 = dbc_fakeUUID_3.getPlayer();
        assertSame("Fucking301", localPlayer.getUserName());
        assertSame(1, localPlayer.getSumCodeScore());
        assertSame(100, localPlayer.getMaxCodeScore());
        assertSame(-101, localPlayer.getMinCodeScore());
        assertSame((double) (2 + 100 - 101) / 3, localPlayer.getAvgCodeScore());
        assertSame("587-379-6201", localPlayer.getContactInfo());
        assertSame(9, localPlayer.getQRCodeList().get(0).showAllComments().size());
        assertSame("comment_1 for code1", localPlayer.getQRCodeList().get(0).showAllComments().get(0));
        assertSame(100, localPlayer.getQRCodeList().get(0).getScore());
        assertSame(8, localPlayer.getQRCodeList().size());
        assertSame(9, fakePlayer_1.getQRCodeList().get(0).showAllComments().size());
        assertSame("comment_2 for code1", localPlayer.getQRCodeList().get(0).showAllComments().get(1));
        assertSame(4, fakePlayer_1.getQRCodeList().size());
        assertSame(2, fakePlayer_2.getQRCodeList().size());     // --> removeCode Checking
        assertSame(2, fakePlayer_2.getTotalCodeNum());          // --> removeCode Checking
        assertSame(100, fakePlayer_1.getQRCodeList().get(0).getScore());
        assertSame(0, fakePlayer_3.getQRCodeList().size());

        // CodesReload (test getGameCodesReload)
        assertSame(localPlayer.getQRCodeList(), dbc_localUUID.getGameCodesReload());
        assertSame(fakePlayer_3.getQRCodeList(), dbc_fakeUUID_3.getGameCodesReload());
        assertSame("comment_1 for code1", dbc_localUUID.getGameCodesReload().get(0).showAllComments().get(0));
    }

}
