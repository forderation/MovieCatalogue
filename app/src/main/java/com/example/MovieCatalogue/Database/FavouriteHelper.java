package com.example.MovieCatalogue.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.MovieCatalogue.PlainOldJavaObject.Movie;
import com.example.MovieCatalogue.PlainOldJavaObject.TVShow;

import java.util.ArrayList;

import static com.example.MovieCatalogue.Database.DatabaseContract.MovieColumns._ID;
import static com.example.MovieCatalogue.Database.DatabaseContract.MovieColumns.idJSON;
import static com.example.MovieCatalogue.Database.DatabaseContract.MovieColumns.isMovie;

public class FavouriteHelper {
    private static final String TABLE_NAME = DatabaseContract.TABLE_MOVIE;
    private static DatabaseHelper dataBaseHelper;
    private static SQLiteDatabase database;

    private FavouriteHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static FavouriteHelper getInstance(Context context){
        return new FavouriteHelper(context);
    }

    public void open() throws SQLException {
      if(database==null){
          database = dataBaseHelper.getWritableDatabase();
      }
      if(!database.isOpen()){
          database = dataBaseHelper.getWritableDatabase();
      }
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    public ArrayList<Long> getMovieFavourites() {
        Cursor cursor = database.query(TABLE_NAME, null, isMovie +" = '1'", null,
                null, null, _ID + " ASC", null);
        cursor.moveToFirst();
        ArrayList<Long> listFavourites = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                listFavourites.add(cursor.getLong(cursor.getColumnIndexOrThrow(idJSON)));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return listFavourites;
    }

    public ArrayList<Long> getTVShowFavourite() {
        Cursor cursor = database.query(TABLE_NAME, null, isMovie +" = '0'", null,
                null, null, _ID + " ASC", null);
        cursor.moveToFirst();
        ArrayList<Long> listFavourites = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                listFavourites.add(cursor.getLong(cursor.getColumnIndexOrThrow(idJSON)));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return listFavourites;
    }

    public long insert(Movie movie) {
        ContentValues movieContent = new ContentValues();
        movieContent.put(idJSON, movie.getId());
        movieContent.put(isMovie,1);
        return database.insert(TABLE_NAME, null, movieContent);
    }

    public long insert(TVShow tvShow) {
        ContentValues movieContent = new ContentValues();
        movieContent.put(idJSON, tvShow.getId());
        movieContent.put(isMovie,0);
        return database.insert(TABLE_NAME, null, movieContent);
    }

    public boolean isMovieFavourites(long id) {
        Cursor cursor = database.query(TABLE_NAME, null, idJSON + " = '" + id + "'" + " AND " + isMovie + "= '1'",
                null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if ((cursor.getLong(cursor.getColumnIndexOrThrow(idJSON))) == id) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public boolean isTVShowFavourites(long id) {
        Cursor cursor = database.query(TABLE_NAME, null, idJSON + " = '" + id + "'" + " AND " + isMovie + "= '0'",
                null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if ((cursor.getLong(cursor.getColumnIndexOrThrow(idJSON))) == id) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public int deleteFavourite(long id) {
        return database.delete(TABLE_NAME, idJSON + " = '" + id + "'", null);
    }
}
