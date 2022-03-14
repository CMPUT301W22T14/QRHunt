
package com.example.qrhunt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


/**
 * This is a class use to form the CustomList object for corresponding listView presentations;
 */
public class CustomList extends ArrayAdapter<GameQRCode> {
    private ArrayList<GameQRCode> codes;
    private Context context;
    private int functionSelection;

    /**
     * This is a constructor function which is used to build up a CustomList object with info given;
     *
     * @param context
     *      This is the context record of the upper level activity, is used to access the view;
     * @param codes
     *      This is an input arraylist which contains all the GameQRCode needed to be added to the
     *     CustomList and be displayed later in the Listview;
     */
    public CustomList(Context context, ArrayList<GameQRCode> codes) {
        super(context,0, codes);
        this.codes = codes;
        this.context = context;
    }


    /**
     * This is a view design function of the CustomList which is called by the Android Project;
     *
     * @param position
     *      The Index position of the specific code inside the arraylist;
     * @param convertView
     *      The view access used for the CustomList;
     * @param parent
     *      The ViewGroup that is used to access the upper level context setting;
     * @return
     *      Return the view designed based on the CustomList setting;
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }
        GameQRCode code = null;
        if (codes.size() != 0){
            code = codes.get(position);
        }
        if (code == null) {
            return view;
        }
        TextView gameQRCodeText = view.findViewById(R.id.game_code_text);
        //TextView gameQRCodeScoreText = view.findViewById(R.id.game_code_other_text);

        gameQRCodeText.setText(code.getContent());
        //gameQRCodeScoreText.setText(code.getScore());   //** FOR TEST
        return view;

    }
}
