package com.example.MovieCatalogue.database;
import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_MOVIE = "favourite_list";
    static final class MovieColumns implements BaseColumns {
        static String idJSONColumn = "id_data";
        static String isMovieColumn = "is_movie";
    }
}
