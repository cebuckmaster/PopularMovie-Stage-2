package com.example.android.popularmovies.utils;

import android.text.TextUtils;

import com.example.android.popularmovies.data.MovieReview;
import com.example.android.popularmovies.data.PopularMovie;
import com.example.android.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is used to break down the json data from themovidedb.org api into an ArrayList of PolularMovies.
 */

public class MovieJsonUtils {

    public static ArrayList<PopularMovie> getMovieTitlesFromJson(String jsonString) throws JSONException {

        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        ArrayList<PopularMovie> movies = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");


            for (int cntr = 0; cntr < resultsArray.length(); cntr++) {
                JSONObject movieDetailsObj = resultsArray.getJSONObject(cntr);
                String title = movieDetailsObj.getString("original_title");
                int movieId = movieDetailsObj.getInt("id");
                String releaseDate = movieDetailsObj.getString("release_date");
                String posterFileName = movieDetailsObj.getString("poster_path");
                double userRating = movieDetailsObj.getDouble("vote_average");
                String overview = movieDetailsObj.getString("overview");

                movies.add(new PopularMovie(title, movieId, releaseDate, posterFileName, userRating, overview));
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public static ArrayList<MovieReview> getMovieReviewFromJson(String jsonString) throws JSONException {

        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        ArrayList<MovieReview> reviews = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");


            for (int cntr = 0; cntr < resultsArray.length(); cntr++) {
                JSONObject movieReviewObj = resultsArray.getJSONObject(cntr);
                String id = movieReviewObj.getString("id");
                String author = movieReviewObj.getString("author");
                String movieReview = movieReviewObj.getString("content");
                String movieUrl = movieReviewObj.getString("url");

                reviews.add(new MovieReview(id, author, movieReview, movieUrl));
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
    }
    public static ArrayList<Trailer> getMovieTrailerFromJson(String jsonString) throws JSONException {

        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");


            for (int cntr = 0; cntr < resultsArray.length(); cntr++) {
                JSONObject movieTrailerObj = resultsArray.getJSONObject(cntr);
                String id = movieTrailerObj.getString("id");
                String key = movieTrailerObj.getString("key");
                String name = movieTrailerObj.getString("name");
                String site = movieTrailerObj.getString("site");
                String type = movieTrailerObj.getString("type");

                trailers.add(new Trailer(id, key, name, site, type));
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return trailers;
    }

}
