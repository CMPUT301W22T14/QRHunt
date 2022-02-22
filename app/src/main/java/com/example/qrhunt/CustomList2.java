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



public class CustomList2 extends ArrayAdapter<String> {

    private ArrayList<String> data;
    private Context context;
    private int functionSelection;

    public CustomList2(Context context, ArrayList<String> data){
        super(context,0, data);
        this.data = data;
        this.context = context;
        this.functionSelection = functionSelection;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }
        String dataItem = null;
        if (data.size() != 0){
            dataItem = data.get(position);
        }
        if (dataItem == null) {
            return view;
        }

        TextView gameQRCodeText = view.findViewById(R.id.game_code_text);
        TextView gameQRCodeScoreText = view.findViewById(R.id.game_code_other_text);

        String scanner = dataItem;
        gameQRCodeText.setText(scanner);
        return view;
    }
}
