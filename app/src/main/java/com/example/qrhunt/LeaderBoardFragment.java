package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This class is the fragment of leader board.
 * Which shows several different ranking that helps player to know about their
 * status in all the players.
 */
public class LeaderBoardFragment extends DialogFragment {
    private Player player = null;
    //private List<Player> players = null;
    private Boolean isPrivacyProtected = false;
    private ListView rankList;
    private TextView rankNameView;
    private TextView myRankView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Players");
    private String TAG = "players";

    // Constructor

    /**
     * This methods sets the player to the fragment.
     * @param player
     *      This is the player that sets to the leader board.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * This is a constructor function which is used to build up a fragment with info given;
     *
     * @param isProtected
     *      input state of the case;
     */
    public LeaderBoardFragment(boolean isProtected) {
        this.isPrivacyProtected = isProtected;
    }

    /**
     * Called by the android system to build up the fragment;
     *
     * @param savedInstanceState
     *      This is the context record of the upper level activity, is used to access the view;
     */
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Locations Marked：
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.leader_board_fragment_layout, null);

        Button highestButton = view.findViewById(R.id.rank_by_QRCode_score_button);
        Button totalNumberButton = view.findViewById(R.id.rank_by_number_own_button);
        Button totalSumButton = view.findViewById(R.id.rank_by_player_total_score_button);
        rankList = view.findViewById(R.id.rank_listView);
        rankNameView = view.findViewById(R.id.rank_name_textView);
        myRankView = view.findViewById(R.id.estimate_ranking_textView);




        /*

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
                    setHighestRank(players);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

         */

        getAndSetHighestRank();

        highestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndSetHighestRank();
            }
        });

        totalNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndSetTotalNumberRank();
            }
        });

        totalSumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndSetSumRank();
            }
        });




        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Leader Board")
                .setNegativeButton("Got it", null).create();

    }

    /**
     * TODO: ...
     */
    public void getAllPlayers() {
        //Yujie's task !!!!!!!!!
    }

    /**
     * This method sets the rank based on player's highest score.
     */
    public void setHighestRank(List<Player> players) {
        //Initialize the highest rank
        rankNameView.setText("Highest Score Rank");
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return -(player1.getMaxCodeScore() - player2.getMaxCodeScore());
            }
        });
        List<String> ranks = new ArrayList<>();
        int number = 1;
        for (Player player : players) {
            String s = "No." + number + ":" + player.getUserName() + "   Score:" + player.getMaxCodeScore();
            ranks.add(s);
            number++;
        }
        ArrayAdapter<String> rankAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, ranks);
        rankList.setAdapter(rankAdapter);

        //int myRank = players.indexOf(player) + 1;
        //String s = "Me:No." + myRank + "   Score:" + player.getMaxCodeScore();
        //myRankView.setText(s);
    }


    /**
     * This method sets the rank based on the number of GameQRCodes the players have.
     */
    public void setTotalNumberRank(List<Player> players) {
        rankNameView.setText("Total Number Rank");
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return -(player1.getTotalCodeNum() - player2.getTotalCodeNum());
            }
        });
        List<String> ranks = new ArrayList<>();
        int number = 1;
        for (Player player : players) {
            String s = "No." + number + ":" + player.getUserName() + "   Score:" + player.getTotalCodeNum();
            ranks.add(s);
            number++;
        }
        ArrayAdapter<String> rankAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, ranks);
        rankList.setAdapter(rankAdapter);

        //int myRank = players.indexOf(player) + 1;
        //String s = "Me:No." + myRank + "   Score:" + player.getTotalCodeNum();
        //myRankView.setText(s);
    }

    /**
     * This method sets the rank based on the player's sum score.
     */
    public void setSumRank(List<Player> players) {
        rankNameView.setText("Total Sum Rank");
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return -(player1.getSumCodeScore() - player2.getSumCodeScore());
            }
        });
        List<String> ranks = new ArrayList<>();
        int number = 1;
        for (Player player : players) {
            String s = "No." + number + ":" + player.getUserName() + "   Score:" + player.getSumCodeScore();
            ranks.add(s);
            number++;
        }
        ArrayAdapter<String> rankAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, ranks);
        rankList.setAdapter(rankAdapter);

        //int myRank = players.indexOf(player) + 1;
        //String s = "Me:No." + myRank + "   Score:" + player.getSumCodeScore();
        //myRankView.setText(s);
    }

    /**
     * This method shows the view of the fragment.
     * @param inflater
     *      This is the layout inflater.
     * @param container
     *      This is the viewGroup container
     * @param savedInstanceState
     *      This is the bundle.
     * @return
     *      The view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.leader_board_fragment_layout, container, false);
    }


    public void getAndSetHighestRank() {
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
                    setHighestRank(players);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getAndSetTotalNumberRank() {
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
                    setTotalNumberRank(players);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getAndSetSumRank() {
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
                    setSumRank(players);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}