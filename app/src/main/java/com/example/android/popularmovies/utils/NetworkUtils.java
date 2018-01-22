package com.example.android.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities are used to access the Network.  The first method is to build the URL needed to call
 * themoviedb.org API.  The second method gets the response from the site into an InputStream and returns
 * it as a json String.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIEDB_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_LOOKUP_POPLAR = "popular";
    private static final String MOVIE_LOOKUP_TOP_RATED = "top_rated";

    private static final String MOVIEDB_REVIEWS_OPTION = "/reviews";
    private static final String MOVIEDB_TRAILER_OPTION = "/videos";

    private static String mMovieBaseUrl;

    final static String API_PARAM = "api_key";
    final static String API_KEY = BuildConfig.MOVIE_DB_API_TOKEN;  //MUST SUPPLY YOUR OWN KEY in gradle.properties file

    public static URL buildUrl(String UrlType, int movieId) {

        switch (UrlType) {
            case "HighestRated":
                mMovieBaseUrl = MOVIEDB_URL + MOVIE_LOOKUP_TOP_RATED;
                break;
            case "MostPopular":
                mMovieBaseUrl = MOVIEDB_URL + MOVIE_LOOKUP_POPLAR;
                break;
            case "MovieReviews":
                mMovieBaseUrl = MOVIEDB_URL + movieId + MOVIEDB_REVIEWS_OPTION;
                break;
            case "MovieTrailer":
                mMovieBaseUrl = MOVIEDB_URL + movieId + MOVIEDB_TRAILER_OPTION;
                break;
            default:
                throw new IllegalArgumentException("Url type is not supported - " + UrlType);
        }
        Uri builtUri = Uri.parse(mMovieBaseUrl).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
