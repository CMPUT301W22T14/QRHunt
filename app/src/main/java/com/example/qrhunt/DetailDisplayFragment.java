package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.qrcode.encoder.QRCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DetailDisplayFragment extends DialogFragment {
    /* Global Variables */
    private GameQRCode gameQRCode = null;


    // Constructor
    public DetailDisplayFragment(GameQRCode gameQRCode) {
        this.gameQRCode = gameQRCode;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Locations Marked：
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.detail_display_fragment_layout, null);

        TextView playerScannedTheSameTextView = view.findViewById(R.id.player_scanned_the_same_QRCode_textView);
        TextView commentsTextView = view.findViewById(R.id.comments_textView);
        ListView playerSameQRCodeList = view.findViewById(R.id.players_sameQRCode_list);
        ListView commentSameQRCodeList = view.findViewById(R.id.comment_sameQRCode_list);

        EditText inputCommentEditText = view.findViewById(R.id.input_comment_editText);
        Button addCommentButton = view.findViewById(R.id.add_comment_button);
        ImageView QRCodeImageView = view.findViewById(R.id.QRCode_view);


        // Filling Data to ListViews:
        ArrayList<String> allScannersList = gameQRCode.showAllScanners();
        ArrayList<String> allCommentsList = gameQRCode.showAllComments();

        ArrayAdapter<String> allScannersAdapter = new CustomList2(getActivity().getBaseContext(), allScannersList);
        ArrayAdapter<String> allCommentsAdapter = new CustomList2(getActivity().getBaseContext(), allCommentsList);

        playerSameQRCodeList.setAdapter(allScannersAdapter);
        commentSameQRCodeList.setAdapter(allCommentsAdapter);


        // Adding Comment (when button clicked):
        // --> Fixing Text Size (limit < 80);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputComment = inputCommentEditText.getText().toString();
                if (inputComment.length() > 80) {
                    inputComment = inputComment.substring(0, 79);
                }
                gameQRCode.addComment(inputComment);
            }
        });


        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Detail Page")
                .setNegativeButton("Looks Good", null).create();





    }


}

