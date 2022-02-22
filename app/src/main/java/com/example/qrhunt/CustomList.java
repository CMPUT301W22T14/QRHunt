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


public class CustomList extends ArrayAdapter<GameQRCode> {

    private ArrayList<GameQRCode> codes;
    private Context context;

    public CustomList(Context context, ArrayList<GameQRCode> codes){
        super(context,0, codes);
        this.codes = codes;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        GameQRCode code = codes.get(position);

        TextView gameQRCodeText = view.findViewById(R.id.game_code_text);
        TextView gameQRCodeScoreText = view.findViewById(R.id.game_code_other_text);

        gameQRCodeText.setText(code.getContent());
        gameQRCodeScoreText.setText(code.getScore());

        return view;

    }
}
