package com.example.qrhunt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class SearchByLocationFragment extends DialogFragment {
    private EditText editLatitude;
    private EditText editLongitude;
    private SearchByLocationFragment.OnFragmentInteractionListener listener;

    /**
     * This is the interface for checking if the search button is pressed.
     */
    public interface OnFragmentInteractionListener{
        void LocationEvent(GameQRCode QRCode);
    }

    /**
     * This method is dealing with actions
     *
     * @param context
     *      get the context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof UsernameSearchFragment.OnFragmentInteractionListener) {
            listener = (SearchByLocationFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Called by the android system to build up the fragment;
     *
     * @param savedInstanceState
     *      Last state record;
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_by_location_fragment_layout, null);
        editLatitude = view.findViewById(R.id.latitude_edit_text);
        editLongitude = view.findViewById(R.id.longitude_edit_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Enter coordinates")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Double latitude = Double.valueOf(editLatitude.getText().toString());
                        Double longitude = Double.valueOf(editLongitude.getText().toString());
                        GameQRCode gameQRCode = new GameQRCode("MAPMANUAL");
                        gameQRCode.setLatitude(latitude);
                        gameQRCode.setLongitude(longitude);
                        listener.LocationEvent(gameQRCode);
                    }
                }).create();
    }

}
