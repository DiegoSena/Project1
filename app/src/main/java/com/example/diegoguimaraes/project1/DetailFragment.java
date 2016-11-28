package com.example.diegoguimaraes.project1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment {

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Movie movie = getActivity().getIntent().getParcelableExtra(Movie.PARCELABLE_KEY);
        TextView textView = (TextView) rootView.findViewById(R.id.detail_textview);
        textView.setText(movie.getTitle());
        ImageView image = (ImageView) rootView.findViewById(R.id.detail_imageview_poster);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/"+ movie.getPosterPath()).into(image);
        TextView releaseYear = (TextView) rootView.findViewById(R.id.detail_textview_releaseyear);
        releaseYear.setText(""+ movie.getReleaseYear());
        TextView average = (TextView) rootView.findViewById(R.id.detail_textview_average);
        average.setText(String.format("%.1f/10", movie.getVoteAverage()));
        TextView overview = (TextView) rootView.findViewById(R.id.detail_textview_overview);
        overview.setText(movie.getOverview());
        return rootView;
    }
}
