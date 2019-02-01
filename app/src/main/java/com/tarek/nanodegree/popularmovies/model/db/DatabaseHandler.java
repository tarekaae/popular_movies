package com.tarek.nanodegree.popularmovies.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tarek.nanodegree.popularmovies.model.pojo.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tarek.abdulkader on 1/21/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fav_movies";

    // Table name
    public static final String TABLE_MOVIES = "movies";

    // Columns
    private String id = "movie_id";
    private String voteCount = "vote_count";
    private String voteAverage = "vote_average";
    private String title = "title";
    private String popularity = "popularity";
    private String posterPath = "poster_path";
    private String originalLanguage = "original_language";
    private String originalTitle = "original_title";
    private String overview = "overview";
    private String releaseDate = "release_date";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE `" + TABLE_MOVIES + "` (\n" +
                "\t`movie_id`\tINTEGER,\n" +
                "\t`vote_count`\tTEXT,\n" +
                "\t`vote_average`\tTEXT,\n" +
                "\t`title`\tTEXT,\n" +
                "\t`popularity`\tTEXT,\n" +
                "\t`poster_path`\tTEXT,\n" +
                "\t`original_language`\tTEXT,\n" +
                "\t`original_title`\tTEXT,\n" +
                "\t`overview`\tTEXT,\n" +
                "\t`release_date`\tTEXT,\n" +
                "PRIMARY KEY(`movie_id`)\n" +
                ");";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);

        onCreate(db);
    }

}
