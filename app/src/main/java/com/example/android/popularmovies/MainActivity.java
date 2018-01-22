package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavMovieDBContract;
import com.example.android.popularmovies.data.PopularMovie;
import com.example.android.popularmovies.utils.MovieJsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] MAIN_MOVIE_PROJECTION = {
            FavMovieDBContract.FavMovieDBEntry._ID,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_TITLE,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_OVERVIEW,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_USER_RATING,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_RELEASE_DATE,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_POSTER_FILENAME,
            FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_POSTER};

    private static final int ID_MOVIE_LOADER = 6;

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private ArrayList<PopularMovie> mMovies;
    private String mSortOrder = "MostPopular";
    private boolean mNetworkErrorFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        int numberOfColumns = 2;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        /**
         * This checks the savedInstanceState on Activity screen change.  If movies is already loaded
         * then we dont want to call the API again we just want to use the movies from the parcalArraylist
         * to load the recyclerview.
         */
        if(savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
            mMovies = new ArrayList<PopularMovie>();
            loadMovieData();
        } else {
            mMovies = savedInstanceState.getParcelableArrayList("Movies");
            showMovieData();
            mMovieAdapter.setMovies(mMovies);
        }
    }

    /**
     * This method calls showMovieData() then run the AsycnTask to load the data from the API. If
     * mSortOrder is Favorites then we will call the CursorLoader to get the favorite movies from the database.
     * If not we will run a background task to get the movies from the MovieDB API
     */
    private void loadMovieData() {

        if (mSortOrder.equals("Favorites")) {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> movieDBLoader = loaderManager.getLoader(ID_MOVIE_LOADER);
            if (movieDBLoader == null) {
                //If loader is null then initialize it else restartLoader
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
            } else {
                getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
            }
        } else {
            showMovieData();
            new FetchMovieDataTask().execute();
        }
    }

    //used to save the ArrayList on screen change so that we can just refresh the data instead of calling API.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Movies", mMovies);
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called when a movie is selected from the parent screen.  This method uses
     * and Intent to call the detail activity to display information about a single movie.  It
     * uses parcelables to pass the movie data to the detail activity.
     * @param movie - object holding the information about the movie that was selected
     *
     *
     */
    @Override
    public void onClick(PopularMovie movie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToShowMovieDetails = new Intent(context, destinationClass);
        intentToShowMovieDetails.putExtra("parcel_data", movie);
        startActivity(intentToShowMovieDetails);

    }

    private void showMovieData() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri favMovieUri = FavMovieDBContract.FavMovieDBEntry.CONTENT_URI;
        return new CursorLoader(this,
                favMovieUri,
                MAIN_MOVIE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            showErrorMessage();
            return;
        }

        if (!mMovies.isEmpty()) {
            mMovies.clear();
        }
        while(cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_TITLE));
            int movieId = cursor.getInt(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID));
            String releasedate = cursor.getString(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_RELEASE_DATE));
            String posterFileName = cursor.getString(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_POSTER_FILENAME));
            double userRating = cursor.getDouble(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_USER_RATING));
            String overview = cursor.getString(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_OVERVIEW));
            byte[] blob = cursor.getBlob(cursor.getColumnIndex(FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_POSTER));

            mMovies.add(new PopularMovie(title, movieId, releasedate, posterFileName, userRating, overview, blob));
        }

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (!mMovies.isEmpty()) {
            if (mNetworkErrorFound) {
                Toast.makeText(this, "Network Error - Displaying Favorite Movies", Toast.LENGTH_LONG).show();
                mNetworkErrorFound= false;
            }
            showMovieData();
            mMovieAdapter.setMovies(mMovies);
        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // This is the AsyncTask to call the Internet API and get the movie data.
    public class FetchMovieDataTask extends AsyncTask<String, Void, ArrayList<PopularMovie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }


        @Override
        protected ArrayList<PopularMovie> doInBackground(String... params) {

            //This builds the URL needed to call the moviedb.org API
            URL movieRequestUrl = NetworkUtils.buildUrl(mSortOrder, 0);

            if (!mMovies.isEmpty()) {
                mMovies.clear();
            }
            mNetworkErrorFound = false;
            //This grabs the jsonMovieresponse and then breakd down the data into an Arraylist of objects called mMovies.
            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                mMovies = MovieJsonUtils.getMovieTitlesFromJson(jsonMovieResponse);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //Once the data is loaded it load the recyclerview adapter with the arraylist of movies.
        @Override
        protected void onPostExecute(ArrayList<PopularMovie> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (!mMovies.isEmpty()) {
                showMovieData();
                mMovieAdapter.setMovies(mMovies);
            } else {
                //If there was a network error then we will display the Movies from the Favorites SQLite Database.
                mNetworkErrorFound = true;
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<String> movieDBLoader = loaderManager.getLoader(ID_MOVIE_LOADER);
                if (movieDBLoader == null) {
                    //if loader does not exist start it
                    getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, MainActivity.this);
                } else {
                    //if loader exists restart it.
                    getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, MainActivity.this);
                }
            }
        }
    }

    //This inflates the Menu to show the Sort Order Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie, menu);
        return true;
    }


    //Based on the selected Sort Order it reloads the movie data by calling loadMovieData() method.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.most_popular:
                mMovieAdapter.setMovies(null);
                mSortOrder = "MostPopular";
                loadMovieData();
                return true;
            case R.id.highest_rated:
                mMovieAdapter.setMovies(null);
                mSortOrder = "HighestRated";
                loadMovieData();
                return true;
            case R.id.favorites:
                mMovieAdapter.setMovies(null);
                mSortOrder = "Favorites";
                loadMovieData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
