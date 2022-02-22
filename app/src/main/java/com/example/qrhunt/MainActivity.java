package com.example.qrhunt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {
    /* Global Variables */
    ListView mainListView = null;
    ArrayList<GameQRCode> mainDataList = new ArrayList<GameQRCode>();
    ArrayAdapter<GameQRCode> mainDataAdapter= null;

    // Acquiring Identification:
    boolean isUUIDExisted = false;
    String uuid = UUID.randomUUID().toString();
    DatabaseConnect dbc = new DatabaseConnect(uuid);
    Player player = null;

    boolean TESTING = true;



    /* Creating Function */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TESTING
        player = new Player("000000");
        GameQRCode gameQRCode1 = new GameQRCode("abc");
        GameQRCode gameQRCode2 = new GameQRCode("edf");
        GameQRCode gameQRCode3 = new GameQRCode("xyz");
        player.addQRCode(gameQRCode1);
        player.addQRCode(gameQRCode2);
        player.addQRCode(gameQRCode3);


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
                                                isUUIDExisted = true;
                                                Toast.makeText(getApplicationContext(), "Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                isUUIDExisted = false;
                                                Toast.makeText(getApplicationContext(), "Sorry, you have no history record on this device...", Toast.LENGTH_LONG).show();
                                                Toast.makeText(getApplicationContext(), "We are creating a new account for this device.", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            break;
                                        // New Player
                                        // --> To ActivityMain;
                                        case 1:
                                            isUUIDExisted = false;
                                            Toast.makeText(getApplicationContext(), "Welcome!! We are creating a new account for your device!", Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            }).create().show();
                            break;
                        case 1:
                            // Scan Player Code:
                            // --> QRCode Scanner Page;
                            uuid = "000000"; // --> from QR scanner
                            isUUIDExisted = true;
                            Toast.makeText(getApplicationContext(), "Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            // Owner:
                            // --> Checking username and password;
                            // --> To Owner Page;
                            isUUIDExisted = false;
                            Toast.makeText(getApplicationContext(), "Welcome to the owner channel", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            // TEST:
                            Toast.makeText(getApplicationContext(), "Welcome to the TEST channel", Toast.LENGTH_LONG).show();



                    }
                }
            }).create().show();
        }

        if (TESTING) {
            ArrayList<GameQRCode> gameQRCodes = player.getQRCodeList();
            mainDataList.addAll(gameQRCodes);
            mainDataAdapter = new CustomList(this, mainDataList);
            mainListView.setAdapter(mainDataAdapter);
        }
        else {
            // Filling the main activity based on roles:
            if (isUUIDExisted) {
                player = dbc.getPlayerReload();
                ArrayList<GameQRCode> gameQRCodes = dbc.getGameCodesReload();
                mainDataList.addAll(gameQRCodes);
                mainDataAdapter = new CustomList(this, mainDataList);
            }
            else {
                mainDataList = null;
                mainDataAdapter = new CustomList(this, mainDataList);
            }
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
                GameQRCode sessionAtPos = mainDataAdapter.getItem(position);

                // Visible Operation:
                button_detail.setVisibility(View.VISIBLE);
                button_delete.setVisibility(View.VISIBLE);


                // DETAIL:
                button_detail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // button_detail functions:
                        // --> detail_display_fragment;
                        // new DetailFragment(sessionAtPos).show(getSupportFragmentManager(), "Detail_Session");

                        // Invisible Operation:
                        button_detail.setVisibility(View.INVISIBLE);
                        button_delete.setVisibility(View.INVISIBLE);
                    }
                });

                // DELETE:
                button_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Removing from ListView:
                        mainDataAdapter.remove(sessionAtPos);

                        // Removing from DataBase:
                        // ...

                        // Invisible Operation:
                        button_detail.setVisibility(View.INVISIBLE);
                        button_delete.setVisibility(View.INVISIBLE);
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
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check condition
        if (intentResult.getContents() != null) {
            //when result content is not null
            GameQRCode gameQRCode = new GameQRCode(intentResult.getContents());
        }
        else {
            //When result content is null
            //Display toast
            Toast.makeText(getApplicationContext(), "OOPS.. You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

}

