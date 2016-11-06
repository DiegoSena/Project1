package com.example.diegoguimaraes.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by diego.guimaraes on 09/10/16.
 */
public class MoviesFragment extends Fragment {

    private MovieAdapter movieAdapter;

    public MoviesFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieAdapter = new MovieAdapter(getContext(), R.id.gridview_movies);
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
        View rootView = inflater.inflate(R.layout.movies_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = movieAdapter.getItem(position);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        new FetchMoviesTask().execute(sharedPreferences.getString(getString(R.string.pref_sort_key),
                                                                 getString(R.string.default_sort_order)));
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private static final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
        private static final String APPID_PARAM = "api_key";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        @Override
        protected Movie[] doInBackground(String... params) {
            String sort = params[0];
            Log.v(LOG_TAG, "sort by " + sort);
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movies = null;

            try{
                String baseUrl = FORECAST_BASE_URL+sort+"/";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "called url " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movies = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movies = null;
                }
                movies = buffer.toString();
            } catch (IOException e) {
                Log.e("FethMovieTask", "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("FethMovieTask", "Error closing stream", e);
                    }
                }
            }

            if(movies != null){
                return getMovies(movies);
            }

            return null;
        }

        private Movie[] getMovies(String movies) {
            try {
                JSONObject moviesJson = new JSONObject(movies);
                JSONArray movieArray = moviesJson.getJSONArray("results");

                Movie[] result = new Movie[movieArray.length()];

                for(int i = 0; i < movieArray.length(); i++ ){
                    JSONObject movie = movieArray.getJSONObject(i);
                    try {
                        result[i] = new Movie(movie.getString("poster_path"),
                                             movie.getString("title"),
                                             getReleaseYear(movie),
                                             movie.getDouble("vote_average"),
                                             movie.getString("overview") );
                    } catch (ParseException e) {
                        Log.e(LOG_TAG, "Error parsing release_date for movie " + movie.getString("title"), e);
                    }
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private int getReleaseYear(JSONObject movie) throws JSONException, ParseException {
            Calendar calendar = Calendar.getInstance();
                  calendar.setTime(sdf.parse(movie.getString("release_date")));
            return calendar.get(Calendar.YEAR);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if(movies != null){
                movieAdapter.clear();
                movieAdapter.addAll(movies);
            }
            movieAdapter.notifyDataSetChanged();
        }
    }
}
