package com.example.android.popularmovies.data;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.example.android.popularmovies.utils.MovieJsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import java.net.URL;
import java.util.ArrayList;

/**
 * TrailerLoader is used to get the movie trailers loaded in a background task.
 */

public class TrailerLoader extends AsyncTaskLoader<ArrayList<Trailer>> {

    private int mMovieId;

    public TrailerLoader(Context context, int movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public ArrayList<Trailer> loadInBackground() {

        ArrayList<Trailer> trailers = new ArrayList<>();

        URL movieTrailerUrl = NetworkUtils.buildUrl("MovieTrailer", mMovieId);

        try {
            String jsonMovieTrailerResponse = NetworkUtils.getResponseFromHttpUrl(movieTrailerUrl);
            trailers = MovieJsonUtils.getMovieTrailerFromJson(jsonMovieTrailerResponse);
            return trailers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
