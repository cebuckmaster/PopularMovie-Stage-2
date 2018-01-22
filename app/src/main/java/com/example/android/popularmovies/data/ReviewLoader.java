package com.example.android.popularmovies.data;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.utils.MovieJsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * ReviewLoader is used to load movie reviews in a background task
 */

public class ReviewLoader extends AsyncTaskLoader<ArrayList<MovieReview>> {

    private int mMovieId;

    public ReviewLoader(final Context context, int movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<MovieReview> loadInBackground() {

        ArrayList<MovieReview> reviews = new ArrayList<>();

        URL movieReviewUrl = NetworkUtils.buildUrl("MovieReviews", mMovieId);

        try {
            String jsonMovieReviewResponse = NetworkUtils.getResponseFromHttpUrl(movieReviewUrl);
            reviews = MovieJsonUtils.getMovieReviewFromJson(jsonMovieReviewResponse);
            return reviews;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
