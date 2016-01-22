package com.example.shihjie_sun.voicecontroller_beta.Movie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shihjie_sun.voicecontroller_beta.R;

import java.util.List;

/**
 * Created by ShihJie_Sun on 2016/1/6.
 */
public class SwipeListAdapter extends BaseAdapter {
    private Activity MovieActivity;
    private LayoutInflater inflater;
    private List<Movie> movieList;
    private String[] bgColors;

    public SwipeListAdapter(Activity MovieActivity, List<Movie> movieList) {
        this.MovieActivity = MovieActivity;
        this.movieList = movieList;
        bgColors = MovieActivity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }


    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int location) {
        return movieList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) MovieActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView serial = (TextView) convertView.findViewById(R.id.serial);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        serial.setText(String.valueOf(movieList.get(position).id));
        title.setText(movieList.get(position).title);

        String color = bgColors[position % bgColors.length];
        serial.setBackgroundColor(Color.parseColor(color));

        return convertView;
    }
}
