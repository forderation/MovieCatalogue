package com.example.moviecatalogue.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "db_movie_favorite";
    private static final int DATABASE_VERSION = 2;
    private static final String SQL_CREATE_TABLE_MOVIE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s INTEGER NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.MovieColumns.TABLE_MOVIE,
            DatabaseContract.MovieColumns._ID,
            DatabaseContract.MovieColumns.idJSON,
            DatabaseContract.MovieColumns.adult,
            DatabaseContract.MovieColumns.backdrop_path,
            DatabaseContract.MovieColumns.original_language,
            DatabaseContract.MovieColumns.original_title,
            DatabaseContract.MovieColumns.overview,
            DatabaseContract.MovieColumns.release_date,
            DatabaseContract.MovieColumns.poster_path,
            DatabaseContract.MovieColumns.vote_average
    );
    private static final String SQL_CREATE_TABLE_TV = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s INTEGER NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TVShowColumns.TABLE_TV_SHOW,
            DatabaseContract.TVShowColumns._ID,
            DatabaseContract.TVShowColumns.idJSON,
            DatabaseContract.TVShowColumns.first_air_date,
            DatabaseContract.TVShowColumns.backdrop_path,
            DatabaseContract.TVShowColumns.original_language,
            DatabaseContract.TVShowColumns.name,
            DatabaseContract.TVShowColumns.overview,
            DatabaseContract.TVShowColumns.original_name,
            DatabaseContract.TVShowColumns.poster_path,
            DatabaseContract.TVShowColumns.vote_average
    );

    DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_MOVIE);
        db.execSQL(SQL_CREATE_TABLE_TV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MovieColumns.TABLE_MOVIE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TVShowColumns.TABLE_TV_SHOW);
        onCreate(db);
    }
}
