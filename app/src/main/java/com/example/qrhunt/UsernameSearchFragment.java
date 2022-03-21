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

/**
 * This class is the fragment for searching other players by their username.
 */
public class UsernameSearchFragment extends DialogFragment {
    private EditText username;
    private OnFragmentInteractionListener listener;

    /**
     * This is the interface for checking if the search button is pressed.
     */
    public interface OnFragmentInteractionListener{
        void onSearchPressed(String uuid);
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
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.username_search_fragment_layout, null);
        username = view.findViewById(R.id.username_search_editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Search player by username")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String uuid = username.getText().toString();
                        listener.onSearchPressed(uuid);
                    }
                }).create();
    }

}
