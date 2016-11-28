package com.example.diegoguimaraes.project1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.AbstractList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by diego.guimaraes on 28/11/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private static final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String APPID_PARAM = "api_key";
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private MovieAdapter movieAdapter;

    public FetchMoviesTask(MovieAdapter movieAdapter) {
        this.movieAdapter = movieAdapter;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        String sort = params[0];
        Log.v(LOG_TAG, "sort by " + sort);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movies = null;
        OkHttpClient client = new OkHttpClient();

        try {
            String baseUrl = FORECAST_BASE_URL + sort + "/";
            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "called url " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

            if(response != null && response.body() != null){
                movies = response.body().string();
            }
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

        if (movies != null) {
            return getMovies(movies);
        }

        return null;
    }

    private Movie[] getMovies(String movies) {
        try {
            JSONObject moviesJson = new JSONObject(movies);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            Movie[] result = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                try {
                    result[i] = new Movie(movie.getString("poster_path"),
                            movie.getString("title"),
                            getReleaseYear(movie),
                            movie.getDouble("vote_average"),
                            movie.getString("overview"));
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
        if (movies != null) {
            movieAdapter.clear();
            movieAdapter.addAll(movies);
        }
        movieAdapter.notifyDataSetChanged();
    }
}
