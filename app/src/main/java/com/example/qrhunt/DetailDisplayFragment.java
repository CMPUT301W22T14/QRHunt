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
    /**
     * This is a constructor function which is used to build up a fragment with info given;
     *
     * @param gameQRCode
     *      This is the context record of the upper level activity, is used to access the view;
     */
    public DetailDisplayFragment(GameQRCode gameQRCode) {
        this.gameQRCode = gameQRCode;
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
        ListView playerSameQRCodeList = view.findViewById(R.id.players_sameQRCode_list);
        ListView commentSameQRCodeList = view.findViewById(R.id.comment_sameQRCode_list);

        EditText inputCommentEditText = view.findViewById(R.id.input_comment_editText);
        Button addCommentButton = view.findViewById(R.id.add_comment_button);
        ImageView QRCodeImageView = view.findViewById(R.id.QRCode_view);

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
            }
        });


        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Detail Page").create();
                //.setNegativeButton("Looks Good", null).create();





    }


}

