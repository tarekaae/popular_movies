package com.tarek.nanodegree.popularmovies.model.providers;

import android.net.Uri;
import android.provider.BaseColumns;

import com.tarek.nanodegree.popularmovies.model.db.DatabaseHandler;

/**
 * Created by tarek.abdulkader on 1/24/2018.
 */

public class MovieContract {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.tarek.nanodegree.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);
    public static final String PATH = DatabaseHandler.TABLE_MOVIES;


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String TABLE_NAME = DatabaseHandler.TABLE_MOVIES;

        // Columns
        public static String column_id = "movie_id";
        public static String column_voteCount = "vote_count";
        public static String column_voteAverage = "vote_average";
        public static String column_title = "title";
        public static String column_popularity = "popularity";
        public static String column_posterPath = "poster_path";
        public static String column_originalLanguage = "original_language";
        public static String column_originalTitle = "original_title";
        public static String column_overview = "overview";
        public static String column_releaseDate = "release_date";

    }

}
