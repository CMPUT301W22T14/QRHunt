package com.example.qrhunt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * This class is the fragment that shows the player's profile.
 * In this fragment, it shows the player's information, QR code for profile and QR code for logging in.
 */
public class ProfileDisplayFragment extends DialogFragment {
    /* Global Variables */
    private Player player = null;
    private Boolean isPrivacyProtected = false;
    private Boolean isVisibleStatus = false;
    private Boolean isVisibleLogin = false;

    /**
     * This method sets the player to the fragment.
     * @param player
     *      The player that need to show the profile.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    // Constructor
    /**
     * This is a constructor function which is used to build up a fragment with info given;
     *
     * @param isProtected
     *      input state of the case;
     */
    public ProfileDisplayFragment(boolean isProtected) {
        this.isPrivacyProtected = isProtected;
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

        TextView changeNameTextView = view.findViewById(R.id.change_name_editText);
        Button changeNameButton = view.findViewById(R.id.change_name_button);
        TextView changeContactInfoTextView = view.findViewById(R.id.change_contactInfo_textView);
        Button changeContactInfoButton = view.findViewById(R.id.change_contactInfo_button);

        generateStatusQRCode(statusQRCodeImage);
        generateLoggingInQRCode(loggingInQRCodeImage);

        /* Protection Operations */
        // Invisible Operations:
        // --> For Searching Result;
        statusQRCodeButton.setVisibility(View.INVISIBLE);
        loggingInQRCodeButton.setVisibility(View.INVISIBLE);
        statusQRCodeImage.setVisibility(View.INVISIBLE);
        loggingInQRCodeImage.setVisibility(View.INVISIBLE);

        // Checking Clicks:
        // --> Show Codes & Images (when clicked);
        if (isPrivacyProtected == false) {
            statusQRCodeButton.setVisibility(View.VISIBLE);
            loggingInQRCodeButton.setVisibility(View.VISIBLE);

            statusQRCodeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isVisibleStatus == false) {
                        // Show Image (statusQRCode):
                        statusQRCodeImage.setVisibility(View.VISIBLE);
                        isVisibleStatus = true;
                    }
                    else {
                        // Hide Image (statusQRCode)
                        statusQRCodeImage.setVisibility(View.INVISIBLE);
                        isVisibleStatus = false;
                    }
                }
            });
            loggingInQRCodeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isVisibleLogin == false) {
                        // Show Image (loggingInQRCode):
                        loggingInQRCodeImage.setVisibility(View.VISIBLE);
                        isVisibleLogin = true;
                    }
                    else {
                        // Hide Image (loggingInQRCode)
                        loggingInQRCodeImage.setVisibility(View.INVISIBLE);
                        isVisibleLogin = false;
                    }
                }
            });
        }

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = changeNameTextView.getText().toString();
                if (!newName.equals("")) {
                    player.setUserName(newName);
                    changeNameTextView.setText("");
                    username.setText("User Name: " + newName);
                }
            }
        });

        changeContactInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newContactInfo = changeContactInfoTextView.getText().toString();
                if (!newContactInfo.equals("")) {
                    player.setContactInfo(newContactInfo);
                    changeContactInfoTextView.setText("");
                    contactInfo.setText("Contact Information: " + newContactInfo);
                }
            }
        });


        // Filling Data to Components:
        username.setText("User Name: " + player.getUserName());
        contactInfo.setText("Contact Information: " + player.getContactInfo());
        minScore.setText("Minimal Score: " + player.getMinCodeScore());
        maxScore.setText("Maximal Score: " + player.getMaxCodeScore());
        avgScore.setText("Average Score: " + player.getAvgCodeScore());
        sumScore.setText("Sum Score: " + player.getSumCodeScore());
        totalNum.setText("Num Of QR Code: " + player.getTotalCodeNum());


        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Profile Page")
                .setNegativeButton("Got it", null).create();

    }

    /**
     * Get the code status;
     *
     * @param statusQRCodeImage
     *      QR code image
     */
    public void generateStatusQRCode(ImageView statusQRCodeImage) {
        //from: youtube.com
        //URL: https://www.youtube.com/watch?v=yJh22Wk74V8
        //Author: https://www.youtube.com/channel/UCklYpZX_-QqHOeSUH4GVQpA

        // Get the information of the player
        String username = "User Name: " + player.getUserName();
        String contactInfo = "Contact Information: " + player.getContactInfo();
        String minScore = "Minimal Score: " + player.getMinCodeScore();
        String maxScore = "Maximal Score: " + player.getMaxCodeScore();
        String avgScore = "Average Score: " + player.getAvgCodeScore();
        String sumScore = "Sum Score: " + player.getSumCodeScore();
        String totalSum = "Num Of QR Code: " + player.getTotalCodeNum();
        // Integrate the information
        String content = "STATUS\n" + username + "\n" + contactInfo + "\n" + minScore + "\n" + maxScore + "\n" +
                avgScore + "\n" + sumScore + "\n" + totalSum;
        // Initialize multi format writer
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            statusQRCodeImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the code status;
     *
     * @param loggingInQRCodeImage
     *      QR code image
     */
    public void generateLoggingInQRCode(ImageView loggingInQRCodeImage) {
        //from: youtube.com
        //URL: https://www.youtube.com/watch?v=yJh22Wk74V8
        //Author: https://www.youtube.com/channel/UCklYpZX_-QqHOeSUH4GVQpA

        String content = "LOGIN\n" + player.getUUID();
        // Initialize multi format writer
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            loggingInQRCodeImage.setImageBitmap(bitmap);

            loggingInQRCodeImage.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

