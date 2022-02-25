package com.example.qrhunt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.UUID;



public class MainActivity extends AppCompatActivity implements UsernameSearchFragment.OnFragmentInteractionListener{
    /* Global Variables */
    ListView mainListView = null;
    ArrayList<GameQRCode> mainDataList = new ArrayList<GameQRCode>();
    ArrayAdapter<GameQRCode> mainDataAdapter= null;

    // Acquiring Identification:
    boolean isUUIDExisted = false;
    String uuid = UUID.randomUUID().toString();
    DatabaseConnect dbc = new DatabaseConnect(uuid);
    Player player = null;

    boolean TESTING = false;     //** FOR TEST



    /* Creating Function */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locations Marked：
        mainListView = findViewById(R.id.session_list);
        final TextView text_rollNum = findViewById(R.id.textview_total_score);
        final FloatingActionButton button_more = findViewById(R.id.button_more);
        final Button button_detail = findViewById(R.id.button_detail);
        final Button button_delete = findViewById(R.id.button_delete);
        final Button button_profile = findViewById(R.id.button_profile);

        // Invisible Operation:
        button_detail.setVisibility(View.INVISIBLE);
        button_delete.setVisibility(View.INVISIBLE);


        // TEST DATASET
        Player testPlayer1 = new Player("000000");
        Player testPlayer2 = new Player("111111");
        GameQRCode gameQRCode1 = new GameQRCode("111");
        GameQRCode gameQRCode2 = new GameQRCode("222");
        GameQRCode gameQRCode3 = new GameQRCode("333");
        GameQRCode gameQRCode4 = new GameQRCode("abc");
        GameQRCode gameQRCode5 = new GameQRCode("zxy");
        testPlayer1.addQRCode(gameQRCode1);
        testPlayer1.addQRCode(gameQRCode2);
        testPlayer1.addQRCode(gameQRCode3);
        testPlayer2.addQRCode(gameQRCode4);
        testPlayer2.addQRCode(gameQRCode5);


        // Identity Asking:
        if (player == null) {
            String[] roleOptions = {"Local Player", "Foreign Player", "Owner Channel", "TEST Channel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Please select your role: ");
            builder.setItems(roleOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int optionIdx) {
                    switch (optionIdx) {
                        case 0:
                            String[] localPlayerOptions = {"Yep, I already have an account", "Nope, I am new here"};
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                            builder2.setTitle("Are you an existed user?");
                            builder2.setItems(localPlayerOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int optionIdx) {
                                    switch (optionIdx) {
                                        // Old Player:
                                        // --> Checking whether data existed or not;
                                        case 0:
                                            // Existed --> Connect to DataBase
                                            // NotExisted --> Create New Account
                                            if (dbc.isDatabaseExisted()) {
                                                //player = dbc.getPlayerReload();
                                                //ArrayList<GameQRCode> gameQRCodes = dbc.getGameCodesReload();
                                                //mainDataList.addAll(gameQRCodes);
                                                player = testPlayer2;                        //** FOR TEST
                                                mainDataList = player.getQRCodeList();      //** FOR TEST
                                                mainDataAdapter = new CustomList(getBaseContext(), mainDataList);
                                                mainListView.setAdapter(mainDataAdapter);
                                                Toast.makeText(getApplicationContext(), "Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                mainDataAdapter = new CustomList(getBaseContext(), null);
                                                mainListView.setAdapter(mainDataAdapter);
                                                Toast.makeText(getApplicationContext(), "Sorry, you have no history record on this device...", Toast.LENGTH_LONG).show();
                                                Toast.makeText(getApplicationContext(), "We are creating a new account for this device.", Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                        // New Player
                                        // --> To ActivityMain;
                                        case 1:
                                            mainDataAdapter = new CustomList(getBaseContext(), null);
                                            mainListView.setAdapter(mainDataAdapter);
                                            Toast.makeText(getApplicationContext(), "Welcome!! We are creating a new account for your device!", Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            }).create().show();
                            break;
                        case 1:
                            // Scan Player Code:
                            // --> QRCode Scanner Page;

                            //player = dbc.getPlayerReload();
                            //ArrayList<GameQRCode> gameQRCodes = dbc.getGameCodesReload();
                            //mainDataList.addAll(gameQRCodes);
                            player = testPlayer2;                        //** FOR TEST
                            mainDataList = player.getQRCodeList();      //** FOR TEST
                            mainDataAdapter = new CustomList(getBaseContext(), mainDataList);
                            mainListView.setAdapter(mainDataAdapter);
                            Toast.makeText(getApplicationContext(), "Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();

                            break;
                        case 2:
                            // Owner:
                            // --> Checking username and password;
                            // --> To Owner Page;
                            mainDataAdapter = new CustomList(getBaseContext(), null);
                            mainListView.setAdapter(mainDataAdapter);
                            Toast.makeText(getApplicationContext(), "Welcome to the owner channel", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            // TEST:
                            // --> Fake DataSet Activated;
                            Toast.makeText(getApplicationContext(), "TEST channel with Fake Data Set Activated", Toast.LENGTH_LONG).show();     //** FOR TEST

                            player = testPlayer1;                        //** FOR TEST
                            mainDataList = player.getQRCodeList();      //** FOR TEST
                            mainDataAdapter = new CustomList(getBaseContext(), mainDataList);
                            mainListView.setAdapter(mainDataAdapter);                           //** FOR TEST
                    }
                }
            }).create().show();
        }



        // MORE:
        button_more.setOnClickListener((view) -> {
            // Asking for function needed:
            String[] functionOptions = {"Scan New Code", "Searching by Location", "Leader Board", "Searching by Username"};
            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
            builder2.setTitle("How can I help you? my friend?");
            builder2.setItems(functionOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int optionIdx) {
                    switch (optionIdx) {
                        case 0:
                            //from: youtube.com
                            //URL: https://www.youtube.com/watch?v=kwOZEU0UBVg
                            //Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
                            
                            // --> Unfolding ProfileDisplayFragment in protected edition;
                            // Scan New Code
                            //Initialize intent integrator
                            IntentIntegrator intentIntegrator = new IntentIntegrator (MainActivity.this);
                            //Set prompt text
                            intentIntegrator.setPrompt("For flash use volume up key");
                            //Set beep
                            intentIntegrator.setBeepEnabled(true);
                            //Locked orientation
                            intentIntegrator.setOrientationLocked(true);
                            //Set capture activity
                            intentIntegrator.setCaptureActivity(Capture.class);
                            //Initiate scan
                            intentIntegrator.initiateScan();
                            break;
                        case 1:
                            // Searching by Location
                            break;
                        case 2:
                            // Leader Board
                            break;
                        case 3:
                            // Searching by Username
                            new UsernameSearchFragment().show(getSupportFragmentManager(), "Search player by username");
                            // --> Unfolding ProfileDisplayFragment;
                            break;
                    }
                }
            }).create().show();
        });



        // Monitoring ListView Clicking:
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Catching the item clicked:
                GameQRCode codeAtPos = mainDataAdapter.getItem(position);

                // Visible Operation:
                button_detail.setVisibility(View.VISIBLE);
                button_delete.setVisibility(View.VISIBLE);


                // DETAIL:
                button_detail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // button_detail functions:
                        // --> detail_display_fragment;
                        new DetailDisplayFragment(codeAtPos).show(getSupportFragmentManager(), "DetailDisplayFragment Activated");

                        // Invisible Operation:
                        button_detail.setVisibility(View.INVISIBLE);
                        button_delete.setVisibility(View.INVISIBLE);
                    }
                });

                // DELETE:
                button_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Removing from ListView:
                        mainDataAdapter.remove(codeAtPos);

                        // Removing from DataBase:
                        //DatabaseConnect dbc =  new DatabaseConnect(uuid);
                        //boolean result = dbc.removeCode(position);
                        boolean result = true;   //** FOR TEST
                        if (result) {
                            Toast.makeText(getApplicationContext(), "Removed Successfully", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Cannot Remove Invalid Code", Toast.LENGTH_LONG).show();
                        }


                        // Invisible Operation:
                        button_detail.setVisibility(View.INVISIBLE);
                        button_delete.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        // PROFILE:
        button_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --> unfolding ProfileDisplayFragment;
                new ProfileDisplayFragment(player, false).show(getSupportFragmentManager(), "ProfileDisplayFragment Activated");

                // Invisible Operation:
                button_detail.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //from: youtube.com
        //URL: https://www.youtube.com/watch?v=kwOZEU0UBVg
        //Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
        super.onActivityResult(requestCode, resultCode, data);
        //Initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check condition
        if (intentResult.getContents() != null) {
            //when result content is not null
            String content = intentResult.getContents();
            //from: stackoverflow.com
            //URL: https://stackoverflow.com/questions/454908/split-java-string-by-new-line
            //Author: https://stackoverflow.com/users/18393/cletus
            String lines[] = content.split("\\r?\\n");
            if (lines[0].equals("STATUS")) {
                // check other player's status
            }
            else if (lines[0].equals("LOGIN")) {
                // login my account in another device
            }
            else {
                // !!! get geolocation

                GameQRCode gameQRCode = new GameQRCode(content);
                mainDataAdapter.add(gameQRCode);

            }
        }

        else {
            //When result content is null
            //Display toast
            Toast.makeText(getApplicationContext(), "OOPS.. You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSearchPressed(String uuid) {
        // Searching if uuid exist in the database:
        // If Exist
        // -> show the profile of uuid;

        DatabaseConnect dbc = new DatabaseConnect(uuid);
        Player playerSearchingResult;
        if (dbc.isDatabaseExisted()){
            playerSearchingResult = dbc.getPlayerReload();
            playerSearchingResult = player;     //** FOR TEST
            new ProfileDisplayFragment(playerSearchingResult, true).show(getSupportFragmentManager(), "ProfileDisplayFragment Activated");
        }
        else {
            Toast.makeText(getApplicationContext(), "User not exist", Toast.LENGTH_LONG).show();
        }
    }



}

