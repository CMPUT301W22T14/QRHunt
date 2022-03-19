package com.example.qrhunt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.provider.Settings.Secure;


/**
 * This is a main class that controls the main activity and connect to all the sub-functions and sub-activities/fragments;
 */
public class MainActivity extends AppCompatActivity implements UsernameSearchFragment.OnFragmentInteractionListener{
    /* Global Variables */
    ListView mainListView = null;
    ArrayList<GameQRCode> mainDataList = new ArrayList<GameQRCode>();
    ArrayAdapter<GameQRCode> mainDataAdapter;

    // Acquiring Identification:
    Fishing fisher = new Fishing();
    String uuid = null;
    Player player = null;
    FireDatabase fdb = null;

    Bitmap captureImage = null;

    // Initialize attributes needed for geolocation
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude;
    double longitude;
    String countryName;
    String locality;
    String address;


    /* Creating Function */
    /**
     * This is the main function of the project, all major tasks, functions and player data are
     *  controlled by the codes below, it also manages the channel toward diverse windows, activities,
     *  fragments and role channels;
     *
     * @param savedInstanceState
     *      Calling by the Android Project to recall the state of application stored in a bundle;
     *
     * */
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

        // Local Info Loading:
        fisher.uuidForLocal = getLocalUUID();
        fisher.fdbForLocal = new FireDatabase(fisher.uuidForLocal);
        fisher.fdbForLocal.isDatabaseExisted(fisher);


        // Identity Asking:
        if (player == null) {
            //String[] roleOptions = {"Local Player", "Foreign Player", "Owner Channel", "TEST Channel"};
            String[] roleOptions = {"Local Player", "Foreign Player", "Owner Channel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Please select your role: ");
            builder.setItems(roleOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int optionIdx) {
                    switch (optionIdx) {
                        // Local Player:
                        case 0:
                            fisher.choice = "LOCAL";
                            uuid = fisher.uuidForLocal;
                            fdb = fisher.fdbForLocal;
                            break;
                        // Foreign Player:
                        case 1:
                            IntentIntegrator intentIntegrator = new IntentIntegrator (MainActivity.this);
                            intentIntegrator.setPrompt("For flash use volume up key");
                            intentIntegrator.setBeepEnabled(true);
                            intentIntegrator.setOrientationLocked(true);
                            intentIntegrator.setCaptureActivity(Capture.class);
                            intentIntegrator.initiateScan();

                            fisher.choice = "INPUT";
                            uuid = fisher.uuidForInput;
                            fisher.fdbForInput = new FireDatabase(uuid);
                            fdb = fisher.fdbForInput;
                            fisher.fdbForInput.isDatabaseExisted(fisher);
                            asynchronousFixed(1);
                            break;
                        // Owner Channel
                        case 2:
                            Toast.makeText(getApplicationContext(), "Welcome to the owner channel!", Toast.LENGTH_LONG).show();
                            break;
                        // TEST Channel
                        /*
                        case 3:
                            isTesting = true;
                            uuid = "TEST";
                            fdb = new FireDatabase(uuid);
                            break;*/
                    }
                    dataLoading();
                }
            }).create().show();
        }
        dataLoading();


        // MORE:
        button_more.setOnClickListener((view) -> {
            // Asking for function needed:
            String[] functionOptions = {"Scan New Code", "Searching by Location", "Leader Board", "Searching by Username", "Map"};
            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
            builder2.setTitle("How can I help you? my friend?");
            builder2.setItems(functionOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int optionIdx) {
                    switch (optionIdx) {
                        case 0:
                            IntentIntegrator intentIntegrator = new IntentIntegrator (MainActivity.this);
                            intentIntegrator.setPrompt("For flash use volume up key");
                            intentIntegrator.setBeepEnabled(true);
                            intentIntegrator.setOrientationLocked(true);
                            intentIntegrator.setCaptureActivity(Capture.class);
                            intentIntegrator.initiateScan();
                            break;
                        case 1:
                            // Searching by Location
                            break;
                        case 2:
                            // Leader Board
                            new ProfileDisplayFragment(player, false).show(getSupportFragmentManager(), "LeaderBoardFragment Activated");
                            break;
                        case 3:
                            // Searching by Username
                            // --> Unfolding ProfileDisplayFragment;
                            new UsernameSearchFragment().show(getSupportFragmentManager(), "Search player by username");
                            break;
                        case 4:
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            }).create().show();
        });



        // Monitoring ListView Clicking:
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * This is a click-checking function for listview items. If any item in the listview is
             *  clicked, the function will record the information of the one selected and set two
             *  buttons (Detail & Delete) on the main page to be visible. If the user taps any of
             *  these two buttons, corresponding actions will be activated (show details of the
             *  listview item selected / delete the listview item selected);
             *
             * @param parent
             *      The adapterView where the action happened;
             * @param view
             *      The view clicked within the Adapter;
             * @param position
             *      The index of the listview item selected by the user;
             * @param id
             *      Presents the row id of the item clicked.;
             * */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Catching the item clicked:
                GameQRCode codeAtPos = mainDataAdapter.getItem(position);

                // Visible Operation:
                button_detail.setVisibility(View.VISIBLE);
                button_delete.setVisibility(View.VISIBLE);


                // --> DETAIL:
                button_detail.setOnClickListener(new View.OnClickListener() {
                    /**
                     * This is a click-checking function for the Detail button. If the user taps the
                     *  button, the monitor will catch that and activate corresponding actions to
                     *  show all the details (unfolding the DetailDisplayFragment) based on the
                     *  listview item selected before. Feedback about the result is displayed with
                     *  buttons to become invisible again;
                     *
                     * @param view
                     *      The view clicked within the Adapter;
                     * */
                    public void onClick(View view) {
                        // button_detail functions:
                        // --> detail_display_fragment;
                        new DetailDisplayFragment(codeAtPos).show(getSupportFragmentManager(), "DetailDisplayFragment Activated");

                        // Invisible Operation:
                        button_detail.setVisibility(View.INVISIBLE);
                        button_delete.setVisibility(View.INVISIBLE);
                    }
                });

                // --> DELETE:
                button_delete.setOnClickListener(new View.OnClickListener() {
                    /**
                     * This is a click-checking function for the Delete button. If the user taps the
                     *  button, the monitor will catch that and activate corresponding actions to
                     *  remove the listview item selected before from both the listview presentation
                     *  and the database remotely. Feedback about the result is displayed with
                     *  buttons to become invisible again;
                     *
                     * @param view
                     *      The view clicked within the Adapter;
                     * */
                    public void onClick(View view) {
                        // Removing from ListView:
                        mainDataAdapter.remove(codeAtPos);
                        Player previousPlayer;
                        // Removing from DataBase:
                        if (fisher.choice.equals("LOCAL")) {
                            previousPlayer = fisher.playerForLocal;
                            fisher.fdbForLocal.removeCode(codeAtPos);
                            asynchronousFixed(1);
                            fdb = fisher.fdbForLocal;
                            player = fisher.playerForLocal;
                        }
                        else {
                            previousPlayer = fisher.playerForInput;
                            fisher.fdbForLocal.removeCode(codeAtPos);
                            asynchronousFixed(1);
                            fdb = fisher.fdbForInput;;
                            player = fisher.playerForInput;
                        }
                        if (previousPlayer == player) {
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
            /**
             * This is a click-checking function for the Profile button. If the user taps the button,
             *  the monitor will catch that and activate corresponding actions to show the user's
             *  profile (unfolding the ProfileDisplayFragment) based on the user's personal info.
             *  Feedback about the result is displayed with buttons to become invisible again;
             *
             * @param view
             *      The view clicked within the Adapter;
             * */
            public void onClick(View view) {
                // --> unfolding ProfileDisplayFragment;
                new ProfileDisplayFragment(player, false).show(getSupportFragmentManager(), "ProfileDisplayFragment Activated");

                // Invisible Operation:
                button_detail.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
            }
        });

        // Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted
            //getLocation();
        } else {
            // when permission denied
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //from: youtube.com
        //URL: https://www.youtube.com/watch?v=kwOZEU0UBVg
        //Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
        if (requestCode == 100) {
            // Get capture image
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            this.captureImage = captureImage;
            return;
        }
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
            else if (lines[0].equals("LOGIN") && lines.length == 2) {
                // login my account in another device

                fisher.uuidForLocal = lines[1];

            }
            else {
                // !!! get geolocation
                // Get image
                // Request for camera Permission
                //from: youtube.com
                //URL: https://www.youtube.com/watch?v=RaOyw84625w&t=250s
                //Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.CAMERA
                    }, 100);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
                GameQRCode gameQRCode = new GameQRCode(content);
                gameQRCode.setCaptureImage(this.captureImage);
                player.addQRCode(gameQRCode);
                GameQRCode redundant = new GameQRCode(" ");
                mainDataAdapter.add(redundant);
                mainDataAdapter.remove(redundant);
                this.captureImage = null;
            }
        }
        else {
            //When result content is null
            //Display toast
            Toast.makeText(getApplicationContext(), "OOPS.. You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This function receives an uuid from the user and searching it from the database to see if the
     *  uuid history exists or not. If any previous records exists, showing the user's profile
     *  (unfolding the ProfileDisplayFragment) based on the user's personal info stored remotely.
     *  Feedback about the result is displayed
     *
     * @param uuid
     *      An uuid inputted by a user to match and check are there any previously existed user info
     *     recorded by the remote database;
     * */
    @Override
    public void onSearchPressed(String uuid) {
        Fishing fisherSpec = new Fishing();
        fisherSpec.uuidForInput = uuid;
        fisherSpec.fdbForInput = new FireDatabase(uuid);

        fisherSpec.fdbForInput.isDatabaseExisted(fisherSpec);
        asynchronousFixed(1);
        if (fisherSpec.isFDBForInputExisted){
            fisherSpec.fdbForInput.getSinglePlayerReload(fisherSpec, null, 4);
            asynchronousFixed(1);
            new ProfileDisplayFragment(fisherSpec.playerForInput, true).show(getSupportFragmentManager(), "ProfileDisplayFragment Activated");
        }
        else {
            Toast.makeText(getApplicationContext(), "User not exist", Toast.LENGTH_LONG).show();
        }
    }


    // Todo: Geolocation Searching Part Is Still Processing...
    /*
    private void getLocation() {
        //From: youtube.com
        // URL:https://www.youtube.com/watch?v=Ak1O9Gip-pg
        // Author: android coding
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initialize Location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        longitude = addresses.get(0).getLongitude();
                        latitude = addresses.get(0).getLatitude();
                        countryName = addresses.get(0).getCountryName();
                        locality = addresses.get(0).getLocality();
                        address = addresses.get(0).getAddressLine(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }*/


    /**
     * This function is used to acquire the device uuid from the local system;
     *
     * @return
     *      The ANDROID_ID (aka uuid) acquired from the system;
     * */
    private String getLocalUUID() {
        //From:stackoverflow.com
        //URL:https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
        //Author: https://stackoverflow.com/users/166712/anthony-forloney
        return Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * This function is used to deal with the player data system, if remote user data record existed,
     *  the function will acquire them and rebuild the player info locally; if not existed, a new
     *  player object will be created and add to the remote database. Besides, we also prepare a
     *  test channel based on testPlayer1 for more local function tests;
     * */
    private void dataLoading() {
        if (fdb != null) {
            fisher.fdbForInput.isDatabaseExisted(fisher);
            asynchronousFixed(1);
            if (fisher.isFDBForLocalExisted || fisher.isFDBForInputExisted) {
                if (fisher.choice.equals("LOCAL")) {
                    fisher.fdbForLocal.getSinglePlayerReload(fisher, null, -2);
                    asynchronousFixed(1);
                    player = fisher.playerForLocal;
                    Toast.makeText(getApplicationContext(), "Local Player: Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                }
                else {
                    fisher.fdbForLocal.getSinglePlayerReload(fisher, null, -1);
                    asynchronousFixed(1);
                    player = fisher.playerForInput;
                    Toast.makeText(getApplicationContext(), "Input Player: Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                player = new Player(fisher.uuidForLocal);
                assert (fisher.choice.equals("LOCAL"));
                fisher.playerForLocal = player;
                fisher.fdbForLocal.addOrUpdatePlayer(player);
                Toast.makeText(getApplicationContext(), "Welcome to join us, new player!", Toast.LENGTH_LONG).show();
                }
            }
            /*
            if (isTesting) {
                player = testPlayer1;
                Toast.makeText(getApplicationContext(), "TEST channel activated", Toast.LENGTH_LONG).show();     //** FOR TEST
            }*/

        mainDataList = player.getQRCodeList();
        mainDataAdapter = new CustomList(getBaseContext(), mainDataList);
        mainListView.setAdapter(mainDataAdapter);
    }

    private void asynchronousFixed(int secondNum) {
        try {
            Toast.makeText(getApplicationContext(), "Task processing...", Toast.LENGTH_LONG).show();
            TimeUnit.SECONDS.sleep(secondNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

