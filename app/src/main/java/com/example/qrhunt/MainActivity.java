package com.example.qrhunt;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Assert;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.provider.Settings.Secure;




/**
 * This is a main class that controls the main activity and connect to all the sub-functions and sub-activities/fragments;
 */
public class MainActivity extends AppCompatActivity implements UsernameSearchFragment.OnFragmentInteractionListener {

    /* Global Variables */
    ListView mainListView = null;
    ArrayList<GameQRCode> mainDataList = new ArrayList<>();
    CustomList mainDataAdapter;
    // Acquiring Identification:
    String uuid;
    FireDatabase fdb = null;
    int choice = -1;
    Boolean existed = false;

    Bitmap captureImage = null;

    // Initialize attributes needed for geolocation
    /*
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude;
    double longitude;
    String countryName;
    String locality;
    String address;*/


    /* Creating Function */

    /**
     * This is the main function of the project, all major tasks, functions and player data are
     * controlled by the codes below, it also manages the channel toward diverse windows, activities,
     * fragments and role channels;
     *
     * @param savedInstanceState Calling by the Android Project to recall the state of application stored in a bundle;
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mainListView.setAdapter(mainDataAdapter);

        // Locations Marked：
        mainListView = findViewById(R.id.session_list);
        final FloatingActionButton button_more = findViewById(R.id.button_more);
        final Button button_detail = findViewById(R.id.button_detail);
        final Button button_delete = findViewById(R.id.button_delete);
        final Button button_profile = findViewById(R.id.button_profile);

        // Invisible Operation:
        button_detail.setVisibility(View.INVISIBLE);
        button_delete.setVisibility(View.INVISIBLE);


        String uuidLocal = getLocalUUID(); //getLocalUUID();

        /*
        if (player == null || player.getUUID().equals("NOT EXIST")) {
            fdb = null;
        }*/

        // Identity Asking:
        if (fdb == null) {
            //String[] roleOptions = {"Local Player", "Foreign Player", "Owner Channel", "TEST Channel"};
            String[] roleOptions = {"Local Player", "Foreign Player", "Owner Channel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Please select your role: ");
            builder.setItems(roleOptions, new DialogInterface.OnClickListener() {
                /**
                 * This is a click method which let user choose what's their identity and status in the game.
                 * The options includes Local Player, Foreign Player and Owner of the game.
                 * @param arg0
                 *      The dialog Interface
                 * @param optionIdx
                 *      The Integer index of the choice
                 */
                @Override
                public void onClick(DialogInterface arg0, int optionIdx) {
                    switch (optionIdx) {
                        // Local Player:
                        case 0:
                            choice = 0;
                            uuid = uuidLocal;
                            break;
                        // Foreign Player:
                        case 1:
                            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                            intentIntegrator.setPrompt("For flash use volume up key");
                            intentIntegrator.setBeepEnabled(true);
                            intentIntegrator.setOrientationLocked(true);
                            intentIntegrator.setCaptureActivity(Capture.class);
                            intentIntegrator.initiateScan();
                            choice = 1;
                            break;
                        // Owner Channel
                        case 2:
                            Toast.makeText(getApplicationContext(), "Welcome to the owner channel!", Toast.LENGTH_LONG).show();
                            break;
                    }
                    fdb = new FireDatabase(uuid);
                    dataHooking();
                }
            }).create().show();
        }
        dataHooking();


        // MORE:
        button_more.setOnClickListener((view) -> {
            // Asking for function needed:
            String[] functionOptions = {"Scan New Code", "Searching by Location", "Leader Board", "Searching by Username", "Map"};
            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
            builder2.setTitle("How can I help you? my friend?");
            builder2.setItems(functionOptions, new DialogInterface.OnClickListener() {
                /**
                 * This is the click method where user could choose what feature they would like to use.
                 * Each option is corresponding to different features, and jump to different fragments.
                 * @param arg0
                 *      The dialog Interface
                 * @param optionIdx
                 *      The integer index of choice
                 */
                @Override
                public void onClick(DialogInterface arg0, int optionIdx) {
                    switch (optionIdx) {
                        case 0:
                            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
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
                            LeaderBoardFragment leaderboard = new LeaderBoardFragment(false);
                            fdb.getSinglePlayerReload(leaderboard, 3, null);
                            asynchronousFixed(1);
                            leaderboard.show(getSupportFragmentManager(), "LeaderBoardFragment Activated");
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
                GameQRCode codeAtPos = mainDataList.get(position);

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
                        mainDataList.remove(codeAtPos);
                        mainDataAdapter.notifyDataSetChanged();
                        // Removing from DataBase:
                        fdb.removeCode(codeAtPos);
                        // Todo: result handle:
                        /*
                        if (result) {
                            Toast.makeText(getApplicationContext(), "Removed Successfully", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Cannot Remove Invalid Code", Toast.LENGTH_LONG).show();
                        }*/

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
                fdb.getSinglePlayerReload(new PlayerCallback() {
                    @Override
                    public void callBack(Player player) {
                        ProfileDisplayFragment profile =  ProfileDisplayFragment.newInstance(false, player);
                        profile.show(getSupportFragmentManager(), "ProfileDisplayFragment Activated");
                        // Invisible Operation:
                        button_detail.setVisibility(View.INVISIBLE);
                        button_delete.setVisibility(View.INVISIBLE);
                    }
                });

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


    /**
     * This is the method for after scanning the code through camera.
     * Analysis the information in the QR code and decide what's the QR code for.
     * @param requestCode
     *      Integer of request code
     * @param resultCode
     *      Integer of result code
     * @param data
     *      The intent which include the QR code data.
     *
     */
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
            } else if (lines[0].equals("LOGIN") && lines.length == 2) {
                // login my account in another device
                uuid = lines[1];
            } else {
                // !!! get geolocation
                // Get image
                // Request for camera Permission
                //from: youtube.com
                //URL: https://www.youtube.com/watch?v=RaOyw84625w&t=250s
                //Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
                }
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 100);
                GameQRCode gameQRCode = new GameQRCode(content);
                gameQRCode.setCaptureImage(this.captureImage);
                fdb.addNewQRCode(gameQRCode);
                GameQRCode redundant = new GameQRCode(" ");
                mainDataList.add(redundant);
                mainDataAdapter.notifyDataSetChanged();
                this.captureImage = null;
            }
        } else {
            //When result content is null
            //Display toast
            Toast.makeText(getApplicationContext(), "OOPS.. You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This function receives an uuid from the user and searching it from the database to see if the
     * uuid history exists or not. If any previous records exists, showing the user's profile
     * (unfolding the ProfileDisplayFragment) based on the user's personal info stored remotely.
     * Feedback about the result is displayed
     *
     * @param uuidTarget An uuid inputted by a user to match and check are there any previously existed user info
     *                   recorded by the remote database;
     */
    @Override
    public void onSearchPressed(String uuidTarget) {
        FireDatabase fdbSpec = new FireDatabase(uuidTarget);
        fdbSpec.getSinglePlayerReload(new PlayerCallback() {
            @Override
            public void callBack(Player player) {
                ProfileDisplayFragment profile =  ProfileDisplayFragment.newInstance(true,player);
                profile.show(getSupportFragmentManager(), "ProfileDisplayFragment Activated");
            }
        });
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
     * @return The ANDROID_ID (aka uuid) acquired from the system;
     */
    private String getLocalUUID() {
        //From:stackoverflow.com
        //URL:https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
        //Author: https://stackoverflow.com/users/166712/anthony-forloney
        return Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * This function is used to deal with the player data system, if remote user data record existed,
     * the function will acquire them and rebuild the player info locally; if not existed, a new
     * player object will be created and add to the remote database.
     */
    /*
    private void dataHooking() {
        if (fdb != null) {
            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = db.collection("Players");
                collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            String uuidGet = doc.getId();
                            if (uuid.equals(uuidGet)) {
                                existed = true;
                                ArrayList<Map<String, Object>> codesOutput = (ArrayList<Map<String, Object>>) (doc.get("codes"));
                                for (Map<String, Object> code : codesOutput) {
                                    GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                    mainDataList.add(newCode);
                                }
                            }
                        }
                        assert existed;
                        // Callback:
                        mainDataAdapter = new CustomList(MainActivity.this, mainDataList);
                        mainListView.setAdapter(mainDataAdapter);
                        Toast.makeText(getApplicationContext(), "Data reloaded successfully. Welcome back!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            catch (Throwable t) {
                fdb.addOrUpdatePlayer(new Player(uuid));
                Toast.makeText(getApplicationContext(), "Welcome to join us, new player!", Toast.LENGTH_LONG).show();
                dataHooking();
            }
        }
        else {
            // owner case;
        }
    }*/


    private void dataHooking() {
        if (fdb != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("Players");
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        String uuidGet = doc.getId();
                        if (uuid.equals(uuidGet)) {
                            mainDataList.clear();
                            existed = true;
                            ArrayList<Map<String, Object>> codesOutput = (ArrayList<Map<String, Object>>) (doc.get("codes"));
                            for (Map<String, Object> code : codesOutput) {
                                GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                mainDataList.add(newCode);
                            }
                        }
                    }
                    try {
                        assert existed;
                        // Callback:
                        mainDataAdapter = new CustomList(MainActivity.this, mainDataList);
                        mainListView.setAdapter(mainDataAdapter);
                        Toast.makeText(getApplicationContext(), "Data Loading...", Toast.LENGTH_LONG).show();
                    }
                    catch (AssertionError ae) {
                        fdb.addOrUpdatePlayer(new Player(uuid));
                        Toast.makeText(getApplicationContext(), "Welcome to join us, new player!", Toast.LENGTH_LONG).show();
                        dataHooking();
                    }
                }
            });

        }
        else {
            // owner case;
        }
    }



    /**
     * The method for fixing the asynchronous problem.
     * @param secondNum
     *      The second need to wait until the asynchronous fix。
     */
    private void asynchronousFixed(int secondNum) {
        try {
            TimeUnit.SECONDS.sleep(secondNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

