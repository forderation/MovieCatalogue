package com.example.moviecatalogue.database;
import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_MOVIE = "favourite_list";
    static final class MovieColumns implements BaseColumns {
        static String idJSON = "id_data";
        static String isMovie = "is_movie";
    }
}
