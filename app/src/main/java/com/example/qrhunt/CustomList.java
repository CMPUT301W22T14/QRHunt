package com.example.qrhunt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


/**
 * This is a class use to form the CustomList object for corresponding listView presentations;
 * This is basically the same thing as the demo in our labs, except that we are displaying differnt
 * objects
 */
public class CustomList extends BaseAdapter {
    private ArrayList<GameQRCode> codes;
    private Context context;



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
        this.context = context;
        this.codes = codes;
    }


    @Override
    public int getCount() {
        return codes.size();
    }

    @Override
    public Object getItem(int i) {
        return codes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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
        View view ;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
            viewHolder. gameQRCodeText = view.findViewById(R.id.game_code_text);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        GameQRCode code = null;
        if (codes.size() != 0){
            code = codes.get(position);
        }
        if (code == null) {
            return view;
        }

        //TextView gameQRCodeScoreText = view.findViewById(R.id.game_code_other_text);

        viewHolder.gameQRCodeText.setText(code.getHash());
        //gameQRCodeScoreText.setText(code.getScore());   //** FOR TEST
        return view;

    }
    class ViewHolder{
        TextView gameQRCodeText;
    }
}
