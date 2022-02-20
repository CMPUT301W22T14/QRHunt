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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;



// For Meeting:
// Todo - Confirmation 01: Dataset Storage format
//
// For Bugs:
//
//




//
public class MainActivity extends AppCompatActivity implements FragmentName.OnFragmentInteractionListener {
    /* Global Variables */
    ListView mainListView;
    ArrayList<String> mainDataList;
    ArrayAdapter<String> mainDataAdapter;


    // <<Creating Function>>
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locations Marked：
        mainListView = findViewById(R.id.session_list);
        final TextView text_rollNum = findViewById(R.id.textview_total_score);
        final Button button_more = findViewById(R.id.button_more);
        final Button button_detail = findViewById(R.id.button_detail);
        final Button button_delete = findViewById(R.id.button_delete);
        final Button button_profile = findViewById(R.id.button_profile);

        // Invisible Operation:
        button_detail.setVisibility(View.INVISIBLE);
        button_delete.setVisibility(View.INVISIBLE);
        button_profile.setVisibility(View.INVISIBLE);

        // Acquiring Device UUID:
        String uuid = UUID.randomUUID().toString();

        // Identity Asking:
        String[] roleOptions = {"Local Player", "Foreign Player", "Owner Channel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Please select your role: ");
        builder.setItems(roleOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int optionIdx) {
                switch (optionIdx) {
                    case 0:
                        String[] localPlayerOptions = {"Nope, I am new here", "Yep, I already have an account"};
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        builder2.setTitle("Are you an existed user?");
                        Arrays.stream(new AlertDialog.Builder[]{builder2.setItems(localPlayerOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int optionIdx) {
                                switch (optionIdx) {
                                    case 0:
                                        // New Player
                                        // --> To ActivityMain;
                                        break;
                                    case 1:
                                        // Old Player
                                        // --> Temporary DataSet
                                        // --> Connect to DataBase
                                        String[] gameQRCode = {"vfr567ujh6gbv9fr2ty", "bvg6frt1y3ui3jh0gty", "nb5fre67u8i4k2jhb3j", "9876t5rfd3vh3ji1hgf"};
                                        mainDataAdapter.addAll(Arrays.asList(gameQRCode));

                                        mainListView.setAdapter(mainDataAdapter);
                                        boolean isValid = true; //dataReload(uuid);
                                        if (isValid) {
                                            // ...
                                            Toast.makeText(getApplicationContext(), "Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Sorry, you have no history record on this device...", Toast.LENGTH_LONG).show();
                                            // ...
                                        }
                                        break;
                                }
                            }
                        })}).create().show();

                        break;
                    case 1:
                        // Scan Player Code
                        // --> QRCode Scanner Page;
                        dataReload(uuid);
                        Toast.makeText(getApplicationContext(), "Welcome back!!", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        // Owner
                        // --> Checking username and password;
                        // --> To Owner Page;
                        Toast.makeText(getApplicationContext(), "Welcome to the owner channel", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }).create().show();


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
                            // Scan New Code
                            //Initialize intent integrator
                            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
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
                button_profile.setVisibility(View.VISIBLE);


                // DETAIL:
                button_detail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // button_detail functions:
                        // --> detail_display_fragment;
                        // new DetailFragment(sessionAtPos).show(getSupportFragmentManager(), "Detail_Session");
                    }
                });

                // DELETE:
                button_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Removing from ListView:
                        mainDataAdapter.remove(sessionAtPos);

                        // Removing from DataBase:
                        // ...
                    }
                });

                // PROFILE:
                button_profile.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // --> profile_display_fragment
                        // new ProfileFragment(sessionAtPos).show(getSupportFragmentManager(), "Profile_Session");
                    }
                });

                // Invisible Operation:
                button_detail.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_profile.setVisibility(View.INVISIBLE);
            }
        });
    }


    private boolean dataReload(String uuid) {
        // success/fail --> return true/false
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Player player;
        mDatabase.child("players").child(uuid).getKey();
        //...
        return false;

    }

    public void addToDatabase(Player player) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Players").child(player.getUUID()).setValue(player);
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
            Toast.makeText(getApplicationContext(), "OOPS.. You did not scan anything",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

