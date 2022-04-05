package com.example.qrhunt;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class MainActivityLocalTest {
    private Solo solo;
    private Activity activity;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<> (MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        activity = rule.getActivity();
    }

    @Test
    public void checkLocalPlayer_ProfileButton() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_profile));
        solo.waitForFragmentByTag("fragment_profile");

        assertTrue(solo.searchText("Profile Page"));
        assertTrue(solo.searchText("User Name:"));

        solo.clickOnView(solo.getView(R.id.my_status_QRCode_button));
        solo.clickOnView(solo.getView(R.id.my_logging_in_QRCode_button));

        solo.enterText((EditText) solo.getView(R.id.change_contactInfo_textView), "CMPUT301");
        solo.clickOnView(solo.getView(R.id.change_contactInfo_button));
        assertTrue(solo.waitForText("CMPUT301", 1, 1000));
    }

    @Test
    public void checkLocalPlayer_DeleteButton() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        try {
            ListView list = rule.getActivity().findViewById(R.id.session_list);
            list.getChildAt(0).performClick();

            solo.clickOnView(solo.getView(R.id.button_delete));
        } catch (Exception e) {
            Log.d(TAG, "Empty Listview");
        }
    }

    @Test
    public void checkLocalPlayer_DetailButton() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        try {
            ListView list = rule.getActivity().findViewById(R.id.session_list);
            list.getChildAt(0).performClick();

            solo.clickOnView(solo.getView(R.id.button_detail));
            solo.waitForFragmentByTag("fragment_detail");
            assertTrue(solo.waitForText("Detail Page", 1, 0));
            assertTrue(solo.waitForText("Comments", 1, 0));
        } catch (Exception e) {
            Log.d(TAG, "Empty Listview");
        }
    }

    @Test
    public void checkLocalPlayer_DetailButton_AddComments() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        try {
            ListView list = rule.getActivity().findViewById(R.id.session_list);
            list.getChildAt(0).performClick();

            solo.clickOnView(solo.getView(R.id.button_detail));
            solo.waitForFragmentByTag("fragment_detail");
            assertTrue(solo.waitForText("Detail Page", 1, 0));
            assertTrue(solo.waitForText("Comments", 1, 0));

            solo.enterText((EditText) solo.getView(R.id.change_contactInfo_textView), "CMPUT301");
            solo.clickOnView(solo.getView(R.id.add_comment_button));
            assertTrue(solo.waitForText("CMPUT301", 1, 5000));
        } catch (Exception e) {
            Log.d(TAG, "Empty Listview");
        }
    }

    @Test
    public void checkLocalPlayer_MoreButton() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        assertTrue(solo.waitForText("Scan New Code", 1, 0));
        assertTrue(solo.waitForText("Leader Board", 1, 0));
        solo.clickOnButton("Cancel");
    }

    @Test
    public void checkLocalPlayer_MoreButton_ScanCode() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        assertTrue(solo.waitForText("Scan Code", 1, 0));
        solo.clickOnButton("Scan Code");
    }

    @Test
    public void checkLocalPlayer_MoreButton_Map() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        assertTrue(solo.waitForText("Map", 1, 0));

        solo.clickOnButton("Map");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
        solo.waitForFragmentByTag("fragment_map", 1000);
    }

    @Test
    public void checkLocalPlayer_MoreButton_LeaderBoard() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        assertTrue(solo.waitForText("Leader Board", 1, 0));

        solo.clickOnButton("Leader Board");
        solo.waitForFragmentByTag("fragment_leaderboard", 1000);

        solo.clickOnView(solo.getView(R.id.rank_by_QRCode_score_button));
        assertTrue(solo.waitForText("Highest Score Rank", 1, 0));
        solo.clickOnView(solo.getView(R.id.rank_by_number_own_button));
        assertTrue(solo.waitForText("Total Number Rank", 1, 0));
        solo.clickOnView(solo.getView(R.id.rank_by_player_total_score_button));
        assertTrue(solo.waitForText("Total Sum Rank", 1, 0));

        solo.clickOnButton("GOT IT");
    }

    @Test
    public void checkLocalPlayer_MoreButton_SearchingByUserName() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        solo.waitForFragmentByTag("fragment_searchByUsername", 1000);
        assertTrue(solo.waitForText("Searching by UserName", 1, 0));
        solo.clickOnButton("Searching by UserName");

        assertTrue(solo.waitForText("Search player by username", 1, 0));

        solo.enterText((EditText) solo.getView(R.id.username_search_editText), "Player 2");
        solo.clickOnButton("SEARCH");
        solo.waitForFragmentByTag("fragment_profile", 1000);
    }

    @Test
    public void checkLocalPlayer_MoreButton_OthersCodes() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        assertTrue(solo.waitForText("Others' Codes", 1, 0));

        solo.clickOnButton("Others' Codes");
        solo.waitForFragmentByTag("fragment_leaderboard", 1000);

        solo.clickOnView(solo.getView(R.id.rank_by_QRCode_score_button));
        assertTrue(solo.waitForText("Highest Score Rank", 1, 0));
        solo.clickOnView(solo.getView(R.id.rank_by_number_own_button));
        assertTrue(solo.waitForText("Total Number Rank", 1, 0));
        solo.clickOnView(solo.getView(R.id.rank_by_player_total_score_button));
        assertTrue(solo.waitForText("Total Sum Rank", 1, 0));

        solo.clickOnButton("GOT IT");
    }

    @Test
    public void checkLocalPlayer_MoreButton_SearchCodesByGeolocation() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Local Player");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_more));
        solo.waitForDialogToOpen(1000);

        assertTrue(solo.waitForText("Search Codes by Geolocation", 1, 0));

        solo.clickOnButton("Search Codes by Geolocation");
        solo.waitForFragmentByTag("fragment_searchByLocation", 1000);

        assertTrue(solo.waitForText("Search Codes By Geolocation", 1, 0));

        solo.enterText((EditText) solo.getView(R.id.latitude_textview), "0");
        solo.enterText((EditText) solo.getView(R.id.longitude_textView), "0");
        solo.clickOnButton("SEARCH");
    }



    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
