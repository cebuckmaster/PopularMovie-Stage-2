package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Favorite Movie ContentProvide class.
 */

public class FavMovieProvider extends ContentProvider {

    private static final int FAVMOVIE = 100;
    private static final int FAVMOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final String LOG_TAG = FavMovieProvider.class.getSimpleName();

    private FavMovieDBHelper  mMovieDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavMovieDBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavMovieDBContract.PATH_MOVIEDB, FAVMOVIE);
        matcher.addURI(authority, FavMovieDBContract.PATH_MOVIEDB+"/#",FAVMOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new FavMovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mMovieDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVMOVIE:
                cursor = database.query(FavMovieDBContract.FavMovieDBEntry.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        sortOrder);
                break;
            case FAVMOVIE_ID:
                selection = FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FavMovieDBContract.FavMovieDBEntry.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVMOVIE:
                return FavMovieDBContract.FavMovieDBEntry.CONTENT_LIST_TYPE;
            case FAVMOVIE_ID:
                return FavMovieDBContract.FavMovieDBEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (match) {
            case FAVMOVIE:
                long id = db.insert(FavMovieDBContract.FavMovieDBEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavMovieDBContract.FavMovieDBEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Insertion is now support for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVMOVIE:
                rowsDeleted = db.delete(FavMovieDBContract.FavMovieDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVMOVIE_ID:
                selection = FavMovieDBContract.FavMovieDBEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(FavMovieDBContract.FavMovieDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        throw new UnsupportedOperationException("Not Implemented");
    }
}
