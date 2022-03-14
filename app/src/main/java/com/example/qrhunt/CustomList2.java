
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
 * This is a class use to form the CustomList2 object for corresponding listView presentations.
 */
public class CustomList2 extends ArrayAdapter<String> {
    private ArrayList<String> data;
    private Context context;

    /**
     * This is a constructor function which is used to build up a CustomList2 object with info given;
     *
     * @param context
     *      This is the context record of the upper level activity, is used to access the view;
     * @param data
     *      This is an input arraylist which contains all the data needed to be added to the
     *     CustomList2 and be displayed later in the corresponding Listview;
     */
    public CustomList2(Context context, ArrayList<String> data){
        super(context,0, data);
        this.data = data;
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
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content2, parent, false);
        }
        String dataItem = null;
        if (data.size() != 0){
            dataItem = data.get(position);
        }
        if (dataItem == null) {
            return view;
        }

        TextView gameQRCodeText = view.findViewById(R.id.game_code_text);
        //TextView gameQRCodeScoreText = view.findViewById(R.id.game_code_other_text);

        String scanner = dataItem;
        gameQRCodeText.setText(scanner);
        return view;
    }
}
