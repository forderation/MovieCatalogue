package com.example.moviecatalogue.database;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String AUTHORITY = "com.forderation.moviecatalogue.favourite";
    public static final String SCHEME = "content";
    public static final class MovieColumns implements BaseColumns {
        public static String TABLE_MOVIE = "movies";
        public static String idJSON = "id_data";
        public static String adult = "adult";
        public static String backdrop_path = "backdrop_path";
        public static String original_language = "original_language";
        public static String original_title = "original_title";
        public static String overview = "overview";
        public static String poster_path = "poster_path";
        public static String vote_average = "vote_average";
        public static String release_date = "release_date";
        public static final Uri CONTENT_URI_MOVIE = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_MOVIE)
                .build();
    }

    public static final class TVShowColumns implements BaseColumns {
        public static String TABLE_TV_SHOW = "tv_shows";
        public static String idJSON = "id_data";
        public static String backdrop_path = "backdrop_path";
        public static String original_language = "original_language";
        public static String name = "name";
        public static String original_name = "original_name";
        public static String overview = "overview";
        public static String poster_path = "poster_path";
        public static String vote_average = "vote_average";
        public static String first_air_date = "first_air_date";
        public static final Uri CONTENT_URI_TV = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_TV_SHOW)
                .build();
    }
}
