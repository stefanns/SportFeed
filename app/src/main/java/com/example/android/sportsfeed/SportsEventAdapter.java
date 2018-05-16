package com.example.android.sportsfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stefan on 5/3/2018.
 */

public class SportsEventAdapter extends ArrayAdapter<SportEvent> {

    public SportsEventAdapter(@NonNull Context context, ArrayList<SportEvent> arrSports) {
        super(context, 0, arrSports);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        final SportEvent sport = getItem(position);
        TextView tvTitle = (TextView) listItemView.findViewById(R.id.news_title);
        TextView tvDate = (TextView) listItemView.findViewById(R.id.news_date);
        TextView tvSection = (TextView) listItemView.findViewById(R.id.section);
        ImageView ivImageNews = (ImageView) listItemView.findViewById(R.id.news_image);
        tvTitle.setText(sport.getTitle());
        tvDate.setText(sport.getDate());
        tvSection.setText(stringUppercase(sport.getSection()));
        ivImageNews.setImageBitmap(sport.getImage());
        return listItemView;
    }

    /*
       method used to capitalize the first letter of a string
     */
    public String stringUppercase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}

