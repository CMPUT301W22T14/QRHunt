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
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class BrowseOthersCodesFragment extends DialogFragment {

    private EditText textView;
    private ListView listView;
    private Button button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Players");
    private String TAG = "players";

    public BrowseOthersCodesFragment() {
        // Required empty public constructor
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_browse_others_codes, null);


        textView = view.findViewById(R.id.other_player_name_textview);
        listView = view.findViewById(R.id.other_players_codes);
        button = view.findViewById(R.id.confirm_search_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uuid =  textView.getText().toString();
                if (!uuid.equals("")) {
                    populateList(uuid);
                }
                textView.setText("");
            }
        });

        // Page Structure:
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Browse Others' Codes").create();

    }

    /**
     * This method populate the list view
     * @param currUuid
     *      The uuid of the current player
     */
    public void populateList(String currUuid) {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> codesHash = new ArrayList<>();
                    codesHash.add(currUuid + "'s codes:");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String uuid = document.getId();
                            if (currUuid.equals(uuid)) {
                                ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                                for (Map<String, Object> code : codes) {
                                    GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                    codesHash.add(newCode.getHash());
                                }
                            }
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    //
                    ArrayAdapter<String> codesAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.content, R.id.game_code_text, codesHash);
                    listView.setAdapter(codesAdapter);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


}