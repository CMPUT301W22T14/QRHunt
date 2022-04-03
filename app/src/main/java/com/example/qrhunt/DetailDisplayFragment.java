package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class is the fragment that shows the detail of a chosen GameQRCode.
 * Which includes the comments, player who scan the same QR Code and so on.
 */
public class DetailDisplayFragment extends DialogFragment {
    /* Global Variables */
    private GameQRCode gameQRCode = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Players");
    private String TAG = "players";
    ListView playerSameQRCodeList;
    ListView commentSameQRCodeList;
    String uuid;
    private CollectionReference collectionReferenceForCodes = db.collection("Codes");


    // Constructor
    /**
     * This is a constructor function which is used to build up a fragment with info given;
     *This function does not return anything, it simply displays the necessary compoenents of the detail
     * page using the parameter that is being passed into this function
     * @param gameQRCode
     *      This is the context record of the upper level activity, is used to access the view;
     */
    public DetailDisplayFragment(GameQRCode gameQRCode, String uuid) {
        this.gameQRCode = gameQRCode;
        this.uuid = uuid;
    }


    /**
     * Called by the android system to build up the fragment;
     *
     * @param savedInstanceState
     *      Last state record;
     */
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Locations Marked：
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.detail_display_fragment_layout, null);

        TextView playerScannedTheSameTextView = view.findViewById(R.id.player_scanned_the_same_QRCode_textView);
        TextView commentsTextView = view.findViewById(R.id.comments_textView);
        playerSameQRCodeList = view.findViewById(R.id.players_sameQRCode_list);
        commentSameQRCodeList = view.findViewById(R.id.comment_sameQRCode_list);
        TextView codeHashTextView = view.findViewById(R.id.code_hash_textview);

        EditText inputCommentEditText = view.findViewById(R.id.input_comment_editText);
        Button addCommentButton = view.findViewById(R.id.add_comment_button);
        ImageView QRCodeImageView = view.findViewById(R.id.QRCode_view);
        codeHashTextView.setText(gameQRCode.getHash());

        // Set QRCodeImageView
        if (gameQRCode.getCaptureImage() != null)
            QRCodeImageView.setImageBitmap(gameQRCode.getCaptureImage());


        // Filling Data to ListViews:

        ArrayAdapter<String> allScannersAdapter = new CustomList2(getActivity().getBaseContext(), gameQRCode.showAllScanners());
        ArrayAdapter<String> allCommentsAdapter = new CustomList2(getActivity().getBaseContext(), gameQRCode.showAllComments());


        playerSameQRCodeList.setAdapter(allScannersAdapter);
        commentSameQRCodeList.setAdapter(allCommentsAdapter);


        // Adding Comment (when button clicked):
        // --> Fixing Text Size (limit < 80);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This click-checking function is for the user to confirm modification of the contact info.
             * It could save the content of editText as the new contact info for player.
             * @param v
             *      The View v.
             */
            public void onClick(View v) {
                String inputComment = inputCommentEditText.getText().toString();
                if (inputComment.equals(""))
                    return;

                inputCommentEditText.setText("");
                if (inputComment.length() > 80) {
                    inputComment = inputComment.substring(0, 79);
                }
                gameQRCode.addComment(inputComment);
                String redundant = " ";
                allCommentsAdapter.add(redundant);
                allCommentsAdapter.remove(redundant);
                uploadComment(inputComment);
            }
        });

        getAllPlayers();
        getAllComments();

        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Detail Page").create();

    }


    public void uploadComment(String comment) {
        DocumentReference documentReference = collectionReferenceForCodes.document(gameQRCode.getHash()+"\n"+uuid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> allComments = new ArrayList<>();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> comments = (ArrayList<String>) document.get("comments");
                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + uuid);
                        if (comments != null) {
                            for (String c : comments) {
                                allComments.add(c);
                            }
                        }
                        allComments.add(comment);
                        documentReference.update("comments", allComments);
                        //newCode.setCaptureImage(image);
                        //Call fragment
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getAllComments() {
        collectionReferenceForCodes.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                List<String> comments = null;
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String id = doc.getId();
                    if (id.equals(gameQRCode.getHash()+"\n"+uuid)) {
                        comments = (ArrayList<String>) doc.get("comments");
                    }
                }

                if (comments != null) {
                    List<String> changed = new ArrayList<>();
                    for (String c : comments) {
                        changed.add("     "+c);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, comments);
                    commentSameQRCodeList.setAdapter(adapter);
                }
            }
        });

    }



    public void getAllPlayers() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Player> players = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String uuid = document.getId();
                            String contactInfo = (String) document.get("contactInfo");
                            ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                            Player player = new Player(uuid); // The player !!!!!!!!!!!!!!
                            player.setContactInfo(contactInfo);
                            for (Map<String, Object> code : codes) {
                                GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                player.addQRCode(newCode);
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                            }
                            players.add(player);
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    List<String> targetPlayers = new ArrayList<>();
                    for (Player player : players) {
                        if (player.hasThisCode(gameQRCode))
                            targetPlayers.add(player.getUUID());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, targetPlayers);
                    playerSameQRCodeList.setAdapter(adapter);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


}

