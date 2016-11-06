package com.example.diegoguimaraes.project1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by diego.guimaraes on 12/10/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, int resource) {
        super(context, 0, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);
        String imageSrc = "http://image.tmdb.org/t/p/w342"+ movie.posterPath;
        Picasso.with(getContext()).load(imageSrc).into(image);
        return convertView;
    }
}