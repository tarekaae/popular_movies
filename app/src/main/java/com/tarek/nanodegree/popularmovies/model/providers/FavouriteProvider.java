package com.tarek.nanodegree.popularmovies.model.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tarek.nanodegree.popularmovies.model.db.DatabaseHandler;

/**
 * Created by tarek.abdulkader on 1/21/2018.
 */

public class FavouriteProvider extends ContentProvider {


    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHandler dbHelper;

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context mContext = getContext();
        dbHelper = new DatabaseHandler(mContext);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri retrnedUri = null;
        switch (match) {
            case MOVIES:

                int id = (int) db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                if (id > 0) {
                    // done
                    retrnedUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Can not insert in the db");
                }
                break;

            default:
                throw new UnsupportedOperationException("please check the provided Uri");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retrnedUri;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retrurnedCursor = null;
        switch (match) {
            case MOVIES:

                retrurnedCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                retrurnedCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("please check the provided Uri");
        }

        retrurnedCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retrurnedCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        int numOfDeletedRows = -1;
        switch (match) {

            case MOVIES:

                numOfDeletedRows = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("please check the provided Uri");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return numOfDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


}
