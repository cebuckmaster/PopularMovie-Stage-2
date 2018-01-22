package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class create the Favorite movie database.
 */

public class FavMovieDBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_FAV_MOVIE_TABLE =
            "CREATE TABLE " + FavMovieDBContract.FavMovieDBEntry.TABLE_NAME + " (" +
                    FavMovieDBContract.FavMovieDBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0," +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_OVERVIEW + " TEXT, " +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_USER_RATING + " REAL, " +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_POSTER_FILENAME + " TEXT, " +
                    FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_POSTER + " BLOB, " +
                    " UNIQUE (" + FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    private static final String SQL_DELETE_FAV_MOVIE_TABLE = "DROP TABLE IF EXISTS " + FavMovieDBContract.FavMovieDBEntry.TABLE_NAME;

    private static final String DATABASE_NAME = "favmovie.db";
    private static final int DATABASE_VERSION = 3;


    public FavMovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAV_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FAV_MOVIE_TABLE);
        onCreate(db);
    }
}
