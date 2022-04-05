package com.example.qrhunt;

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


public class OwnerActivityTest {
    private static final String TAG = "Exception";
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
    public void checkPlayerTypeDialogue() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.waitForDialogToOpen();
    }

    @Test
    public void checkProfilePage() {
        solo.waitForDialogToOpen();
        solo.clickOnButton("Owner Channel");
        solo.assertCurrentActivity("Wrong Activity", OwnerActivity.class);

        try{
            ListView list1 = rule.getActivity().findViewById(R.id.players_list);
            list1.getChildAt(0).performClick();
            solo.clickOnView(solo.getView(R.id.button_delete_player));

            ListView list2 = rule.getActivity().findViewById(R.id.players_list);
            list2.getChildAt(0).performClick();
            ListView list3 = rule.getActivity().findViewById(R.id.codes_list);
            list3.getChildAt(0).performClick();
            solo.clickOnView(solo.getView(R.id.button_delete_code));
        }
        catch (Exception e) {
            Log.d(TAG, "checkProfilePage: EMPTY LISTVIEW");
        }
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


}
