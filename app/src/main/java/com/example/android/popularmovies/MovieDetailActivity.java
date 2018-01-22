package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.data.FavMovieDBContract;
import com.example.android.popularmovies.data.MovieReview;
import com.example.android.popularmovies.data.PopularMovie;
import com.example.android.popularmovies.data.ReviewLoader;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.data.TrailerLoader;
import com.example.android.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * This is the Detail Activity Method to display an individual Movie.  This is started by an
 * intent from the main activity which provides this activity the movie that was clicked on in a parcelable Object.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final int MOVIE_LOADER = 0;
    private static final int MOVIE_REVIEW_LOADER = 2;
    private static final int MOVIE_TRAILER_LOADER = 3;
    private static final String QUERY_MOVIE_ID = "movieId";

    private TextView mDetailMovieTitle;
    private ImageView mDetailMovieImg;
    private TextView mDetailUserRating;
    private TextView mDetailOverview;
    private TextView mDetailReleaseDate;
    private Button mFavMovieButton;
    private int mMovieId;
    private String mMoviePosterURL;
    private TextView mReview1TextView;
    private View mReview1Divider;
    private TextView mReview2TextView;
    private View mReview2Divider;
    private TextView mReview3TextView;
    private TextView mReview1Author;
    private TextView mReview2Author;
    private TextView mReview3Author;
    private Button mTrailer1Btn;
    private Button mTrailer2Btn;
    private Button mTrailer3Btn;
    private TextView mTrailer1TextView;
    private TextView mTrailer2TextView;
    private TextView mTrailer3TextView;
    private View mTrailer1Divider;
    private View mTrailer2Divider;
    private TextView mTrailerErrorMsg;
    private TextView mReviewErrorMsg;

    private ArrayList<Trailer> mTrailers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);


        mDetailMovieImg = (ImageView) findViewById(R.id.iv_detail_movie_img);
        mDetailMovieTitle = (TextView) findViewById(R.id.tv_detail_movie_title);
        mDetailUserRating = (TextView) findViewById(R.id.tv_detail_user_rating);
        mDetailReleaseDate = (TextView) findViewById(R.id.tv_detail_release_date);
        mDetailOverview = (TextView) findViewById(R.id.tv_detail_overview);
        mReview1TextView = (TextView) findViewById(R.id.tv_review1);
        mReview2TextView = (TextView) findViewById(R.id.tv_review2);
        mReview3TextView = (TextView) findViewById(R.id.tv_review3);
        mReview1Divider = (View)findViewById(R.id.viewReview1Divider);
        mReview2Divider = (View)findViewById(R.id.viewReview2Divider);
        mReview1Author = (TextView)findViewById(R.id.tv_review1_author);
        mReview2Author = (TextView)findViewById(R.id.tv_review2_author);
        mReview3Author = (TextView)findViewById(R.id.tv_review3_author);
        mTrailer1Btn = (Button) findViewById(R.id.trailer1_btn);
        mTrailer2Btn = (Button) findViewById(R.id.trailer2_btn);
        mTrailer3Btn = (Button) findViewById(R.id.trailer3_btn);
        mTrailer1TextView = (TextView) findViewById(R.id.tv_trailer1__label);
        mTrailer2TextView = (TextView) findViewById(R.id.tv_trailer2_label);
        mTrailer3TextView = (TextView) findViewById(R.id.tv_trailer3_label);
        mTrailer1Divider = (View) findViewById(R.id.viewTrailer1Divider);
        mTrailer2Divider = (View) findViewById(R.id.viewTrailer2Divider);
        mReviewErrorMsg = (TextView) findViewById(R.id.tv_review_error_msg);
        mTrailerErrorMsg = (TextView) findViewById(R.id.tv_trailer_error_msg);


        final Context context = this;
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("parcel_data")) {
            PopularMovie movie = (PopularMovie) intentThatStartedThisActivity.getParcelableExtra("parcel_data");
            mDetailMovieTitle.setText(movie.getTitle());
            mMovieId = movie.getMovieId();
            mDetailReleaseDate.setText(movie.getReleaseDate());
            mMoviePosterURL = movie.getImageURL();
            mDetailUserRating.setText(Double.toString(movie.getUserRating()));
            mDetailOverview.setText(movie.getOverview());

            if (movie.getMoviePoster() == null) {
                Picasso.with(context).load(mMoviePosterURL).into(mDetailMovieImg);
            } else {
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(movie.getMoviePoster());
                    Bitmap storedBitMap = BitmapFactory.decodeStream(inputStream);
                    mDetailMovieImg.setImageBitmap(storedBitMap);
                } catch (Exception e) {
                    Log.e ("MovieDetailAdapter", "Error trying to load image - " + e);
                }
            }


        }

        Bundle reviewBundle = new Bundle();
        reviewBundle.putInt(QUERY_MOVIE_ID, mMovieId);

        getSupportLoaderManager().initLoader(MOVIE_REVIEW_LOADER, reviewBundle, loaderCallbackMovieReviews);
        getSupportLoaderManager().initLoader(MOVIE_TRAILER_LOADER, reviewBundle, loaderCallbacksTrailers);


        mFavMovieButton = (Button) findViewById(R.id.fav_movie_button);

        unSetFavMovieButton();

        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, loaderCallbackCursorLoader);

        mFavMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonText = mFavMovieButton.getText().toString();
                if (buttonText.equals(getResources().getString(R.string.fav_button_text))) {
                    deleteFavMovie();
                } else {
                    addFavMovie();
                }
            }
        });

        mTrailer1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchVideo(0);
            }
        });
        mTrailer2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchVideo(1);
            }
        });
        mTrailer3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchVideo(2);
            }
        });

    }

    public void watchVideo(int buttonId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailers.get(buttonId).getAppLink()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailers.get(buttonId).getWebLink()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    /**
     *    This override is used to change the function of the Up action when pressed.  We dont want the main activity
     *    to call the API again and revert back to the default sort order.  This changes the Up action to be the same as
     *    back pressed so the main activity isnt rebuilt.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean addFavMovie() {

        byte[] img = null;

        String movieTitle = mDetailMovieTitle.getText().toString();
        int movieId = mMovieId;
        String releaseDate = mDetailReleaseDate.getText().toString();
        String posterFileName = mMoviePosterURL;
        String userRating = mDetailUserRating.getText().toString();
        String overview = mDetailOverview.getText().toString();
        try {
            Bitmap image = ((BitmapDrawable) mDetailMovieImg.getDrawable()).getBitmap();
            img = Utils.getBitmapAsByteArray(image);
        } catch (Exception e) {
            Log.e("detailActivity", "Error getting bitmap - " + e);
        }

        ContentValues values = new ContentValues();
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_TITLE, movieTitle);
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID, movieId);
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_POSTER_FILENAME, posterFileName);
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_USER_RATING, userRating);
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_OVERVIEW, overview);
        values.put(FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_POSTER, img);
        Uri newUri = getContentResolver().insert(FavMovieDBContract.FavMovieDBEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, "Failed to save favorite movie", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(this, "Movie was added to your favorites", Toast.LENGTH_SHORT).show();
            setFavMovieButton();
            return true;
        }
    }

    public void deleteFavMovie() {
        String selection = Integer.toString(mMovieId);
        Uri currentMovieUri = FavMovieDBContract.FavMovieDBEntry.buildMovieUriWithId(mMovieId);
        int rowsDeleted = getContentResolver().delete(currentMovieUri, selection, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Failed to remove movie from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            unSetFavMovieButton();
            Toast.makeText(this, "Removed movie from Favorites", Toast.LENGTH_LONG).show();
        }
    }

    public void setFavMovieButton() {
        mFavMovieButton.setText(getResources().getString(R.string.fav_button_text));
        mFavMovieButton.setBackgroundColor(getResources().getColor(R.color.favMovieColor));
    }
    public void unSetFavMovieButton() {
        mFavMovieButton.setText(getResources().getString(R.string.mark_as_favorite));
        mFavMovieButton.setBackgroundColor(getResources().getColor(R.color.notfavMovieColor));
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbackCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    FavMovieDBContract.FavMovieDBEntry._ID,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_TITLE,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_OVERVIEW,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_USER_RATING,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_RELEASE_DATE,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_POSTER_FILENAME,
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_POSTER};

            String selection = Integer.toString(mMovieId);
            Uri currentMovieUri = FavMovieDBContract.FavMovieDBEntry.buildMovieUriWithId(mMovieId);
            Context context = getApplicationContext();

            return new CursorLoader(context,
                    currentMovieUri,
                    projection,
                    selection,
                    null,
                    null);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }

            setFavMovieButton();

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    private LoaderManager.LoaderCallbacks<ArrayList<MovieReview>> loaderCallbackMovieReviews = new LoaderManager.LoaderCallbacks<ArrayList<MovieReview>>() {
        @Override
        public Loader<ArrayList<MovieReview>> onCreateLoader(int id, Bundle args) {
            Context context = getApplicationContext();
            return new ReviewLoader(context, args.getInt(QUERY_MOVIE_ID));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieReview>> loader, ArrayList<MovieReview> reviews) {

            if (reviews == null || reviews.size() == 0) {
                mReviewErrorMsg.setVisibility(View.VISIBLE);
                mReview1Author.setVisibility(View.GONE);
                mReview1TextView.setVisibility(View.GONE);
                mReview1Divider.setVisibility(View.GONE);
                mReview2Author.setVisibility(View.GONE);
                mReview2TextView.setVisibility(View.GONE);
                mReview2Divider.setVisibility(View.GONE);
                mReview3Author.setVisibility(View.GONE);
                mReview3TextView.setVisibility(View.GONE);
                return;
            }

            mReviewErrorMsg.setVisibility(View.GONE);

            if (reviews.size() >= 1 &&  reviews.get(0).getReview() != null) {
                mReview1Author.setText(reviews.get(0).getAuthor());
                mReview1TextView.setText(reviews.get(0).getReview());
            }
            if (reviews.size() >= 2 &&  reviews.get(1).getReview() != null) {
                mReview2Author.setText(reviews.get(1).getAuthor());
                mReview2TextView.setText(reviews.get(1).getReview());
            } else {
                mReview1Divider.setVisibility(View.GONE);
                mReview2Author.setVisibility(View.GONE);
                mReview2TextView.setVisibility(View.GONE);
                mReview2Divider.setVisibility(View.GONE);
                mReview3TextView.setVisibility(View.GONE);
            }
            if (reviews.size() >= 3 &&  reviews.get(2).getReview() != null) {
                mReview3Author.setText(reviews.get(2).getAuthor());
                mReview3TextView.setText(reviews.get(2).getReview());
            } else {
                mReview2Divider.setVisibility(View.GONE);
                mReview3Author.setVisibility(View.GONE);
                mReview3TextView.setVisibility(View.GONE);
            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieReview>> loader) {
            return;
        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<Trailer>> loaderCallbacksTrailers = new LoaderManager.LoaderCallbacks<ArrayList<Trailer>>() {
        @Override
        public Loader<ArrayList<Trailer>> onCreateLoader(int id, Bundle args) {
            Context context = getApplicationContext();
            return new TrailerLoader(context, args.getInt(QUERY_MOVIE_ID));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> trailers) {

            if (trailers == null || trailers.size() == 0) {
                mTrailerErrorMsg.setVisibility(View.VISIBLE);
                mTrailer1Divider.setVisibility(View.GONE);
                mTrailer1TextView.setVisibility(View.GONE);
                mTrailer1Btn.setVisibility(View.GONE);
                mTrailer2Btn.setVisibility(View.GONE);
                mTrailer2TextView.setVisibility(View.GONE);
                mTrailer2Divider.setVisibility(View.GONE);
                mTrailer3Btn.setVisibility(View.GONE);
                mTrailer3TextView.setVisibility(View.GONE);
                return;
            }

            mTrailers = new ArrayList<>(trailers);

            if (trailers.size() >= 1 &&  trailers.get(0).getVideoKey() != null) {
                mTrailer1TextView.setText(trailers.get(0).getVideoName());
            }

            if (trailers.size() >= 2 &&  trailers.get(1).getVideoKey() != null) {
                mTrailer2TextView.setText(trailers.get(1).getVideoName());
            } else {
                mTrailer2Btn.setVisibility(View.GONE);
                mTrailer2TextView.setVisibility(View.GONE);
                mTrailer2Divider.setVisibility(View.GONE);
                mTrailer3Btn.setVisibility(View.GONE);
                mTrailer3TextView.setVisibility(View.GONE);
            }

            if (trailers.size() >= 3 &&  trailers.get(2).getVideoKey() != null) {
                mTrailer3TextView.setText(trailers.get(2).getVideoName());
            } else {
                mTrailer3Btn.setVisibility(View.GONE);
                mTrailer3TextView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {
            return;
        }
    };
}
