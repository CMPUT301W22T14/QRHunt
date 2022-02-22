package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;



public class ProfileDisplayFragment extends DialogFragment {
    /* Global Variables */
    private Player player = null;
    private Boolean isPrivacyProtected = false;


    // Constructor
    public ProfileDisplayFragment(Player player, boolean isProtected) {
        this.player = player;
        this.isPrivacyProtected = isProtected;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Locations Marked：
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_display_fragment_layout, null);

        TextView username = view.findViewById(R.id.user_name_textView);
        TextView contactInfo = view.findViewById(R.id.contact_information_textView);

        TextView minScore = view.findViewById(R.id.min_score_textView);
        TextView maxScore = view.findViewById(R.id.max_score_textView);
        TextView avgScore = view.findViewById(R.id.average_score_textView);
        TextView sumScore = view.findViewById(R.id.sum_score_textView);
        TextView totalNum = view.findViewById(R.id.total_QRCode_textView);

        Button statusQRCodeButton = view.findViewById(R.id.my_status_QRCode_button);
        Button loggingInQRCodeButton = view.findViewById(R.id.my_logging_in_QRCode_button);
        ImageView statusQRCodeImage = view.findViewById(R.id.my_status_QRCode_image);
        ImageView loggingInQRCodeImage = view.findViewById(R.id.my_logging_in_QRCode_image);


        /* Protection Operations */
        // Invisible Operations:
        // --> For Searching Result;
        statusQRCodeButton.setVisibility(View.INVISIBLE);
        loggingInQRCodeButton.setVisibility(View.INVISIBLE);
        statusQRCodeImage.setVisibility(View.INVISIBLE);
        loggingInQRCodeImage.setVisibility(View.INVISIBLE);

        // Checking Clicks:
        // --> Show Codes & Images (when clicked);
        if (isPrivacyProtected = false) {
            statusQRCodeButton.setVisibility(View.VISIBLE);
            loggingInQRCodeButton.setVisibility(View.VISIBLE);

            statusQRCodeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Show Image (statusQRCode):
                    statusQRCodeImage.setVisibility(View.VISIBLE);
                }
            });
            loggingInQRCodeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Show Image (loggingInQRCode):
                    loggingInQRCodeImage.setVisibility(View.VISIBLE);
                }
            });
        }


        // Filling Data to Components:
        username.setText("User Name: " + player.getUserName());
        contactInfo.setText("Contact Information: " + player.getContactInfo());
        minScore.setText("Minimal Score: " + player.getMinCodeScore());
        maxScore.setText("Maximal Score: " + player.getMaxCodeScore());
        avgScore.setText("Average Score: " + player.getAvgCodeScore());
        sumScore.setText("Sum Score: " + player.getSumCodeScore());
        totalNum.setText("SessionDate: " + player.getTotalCodeNum());


        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Profile Page")
                .setNegativeButton("Got it", null).create();

    }
}

