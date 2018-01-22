package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cebuc on 12/20/2017.
 */

public class PopularMovie implements Parcelable {

    private String mTitle;
    private int mMovieId;
    private String mReleaseDate;
    private String mPosterFileName;
    private double mUserRating;
    private String mOverview;
    private byte[] mMoviePoster;

    public PopularMovie(String title, int movieId, String releaseDate, String posterFileName, double userRating, String overview) {
        mTitle = title;
        mMovieId = movieId;
        mReleaseDate = releaseDate;
        mPosterFileName = posterFileName;
        mUserRating = userRating;
        mOverview = overview;
    }

    public PopularMovie(String title, int movieId, String releaseDate, String posterFileName, double userRating, String overview, byte[] moviePoster) {
        mTitle = title;
        mMovieId = movieId;
        mReleaseDate = releaseDate;
        mPosterFileName = posterFileName;
        mUserRating = userRating;
        mOverview = overview;
        mMoviePoster = moviePoster;
    }

    private PopularMovie(Parcel in) {
        this.mTitle = in.readString();
        this.mMovieId = in.readInt();
        this.mReleaseDate = in.readString();
        this.mPosterFileName = in.readString();
        this.mUserRating = in.readDouble();
        this.mOverview = in.readString();


        final int movieByteLen = in.readInt();
        if (movieByteLen <= 0) {
            this.mMoviePoster = null;
        } else {
            this.mMoviePoster = new byte[movieByteLen];
            in.readByteArray(this.mMoviePoster);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public int getMovieId() {
        return mMovieId;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
    public String getImageURL() {
        if (mPosterFileName.contains("http:")) {
            return mPosterFileName;
        }
        return "http://image.tmdb.org/t/p/w185/" + mPosterFileName;
    }
    public double getUserRating() {

        return mUserRating;
    }
    public String getOverview() {
        return mOverview;
    }

    public byte[] getMoviePoster() {
        return mMoviePoster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeInt(mMovieId);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mPosterFileName);
        parcel.writeDouble(mUserRating);
        parcel.writeString(mOverview);

        if (mMoviePoster != null) {
            parcel.writeInt(mMoviePoster.length);
            parcel.writeByteArray(mMoviePoster);
        }
    }

    public static final Parcelable.Creator<PopularMovie> CREATOR = new Parcelable.Creator<PopularMovie>() {

        @Override
        public PopularMovie createFromParcel(Parcel in) {
            return new PopularMovie(in);
        }

        @Override
        public PopularMovie[] newArray(int size) {
            return new PopularMovie[size];
        }
    };


}
