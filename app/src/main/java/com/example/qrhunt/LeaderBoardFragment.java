package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LeaderBoardFragment extends DialogFragment {
    private Player player = null;
    private List<Player> players = null;
    private Boolean isPrivacyProtected = false;
    private ListView rankList;
    private TextView rankNameView;
    private TextView myRankView;

    // Constructor

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * This is a constructor function which is used to build up a fragment with info given;
     *
     * @param player
     *      input player
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


        getAllPlayers();
        setHighestRank();

        highestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHighestRank();
            }
        });

        totalNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTotalNumberRank();
            }
        });

        totalSumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSumRank();
            }
        });


        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Leader Board")
                .setNegativeButton("Got it", null).create();

    }

    public void getAllPlayers() {
        //Yujie's task !!!!!!!!!
    }

    public void setHighestRank() {
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
        ArrayAdapter<String> rankAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, ranks);
        rankList.setAdapter(rankAdapter);

        int myRank = players.indexOf(player) + 1;
        String s = "Me:No." + myRank + "   Score:" + player.getMaxCodeScore();
        myRankView.setText(s);
    }


    public void setTotalNumberRank() {
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
        ArrayAdapter<String> rankAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, ranks);
        rankList.setAdapter(rankAdapter);

        int myRank = players.indexOf(player) + 1;
        String s = "Me:No." + myRank + "   Score:" + player.getTotalCodeNum();
        myRankView.setText(s);
    }

    public void setSumRank() {
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
        ArrayAdapter<String> rankAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, ranks);
        rankList.setAdapter(rankAdapter);

        int myRank = players.indexOf(player) + 1;
        String s = "Me:No." + myRank + "   Score:" + player.getSumCodeScore();
        myRankView.setText(s);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.leader_board_fragment_layout, container, false);
    }
}