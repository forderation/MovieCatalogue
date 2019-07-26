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
import static com.example.MovieCatalogue.Database.DatabaseContract.MovieColumns.idJSONColumn;
import static com.example.MovieCatalogue.Database.DatabaseContract.MovieColumns.isMovieColumn;

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

    public ArrayList<Long> getFavourites(boolean isMovie) {
        final int isMovieIndicator = isMovie? 1:0;
        Cursor cursor = database.query(TABLE_NAME, null, isMovieColumn +" = '"+isMovieIndicator+"'", null,
                null, null, _ID + " ASC", null);
        cursor.moveToFirst();
        ArrayList<Long> listFavourites = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                listFavourites.add(cursor.getLong(cursor.getColumnIndexOrThrow(idJSONColumn)));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return listFavourites;
    }

    public long insert(long idJSon, boolean isMovie) {
        final int isMovieIndicator = isMovie? 1:0;
        ContentValues movieContent = new ContentValues();
        movieContent.put(idJSONColumn, idJSon);
        movieContent.put(isMovieColumn,isMovieIndicator);
        return database.insert(TABLE_NAME, null, movieContent);
    }

    public boolean isFavourite(long id, boolean isMovie) {
        final int isMovieIndicator = isMovie? 1:0;
        final String selection = idJSONColumn + " = '"+ id + "'" + " AND " + isMovieColumn + "= '" + isMovieIndicator +"'";
        Cursor cursor = database.query(TABLE_NAME, null, selection,null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if ((cursor.getLong(cursor.getColumnIndexOrThrow(idJSONColumn))) == id) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public int deleteFavourite(long id) {
        return database.delete(TABLE_NAME, idJSONColumn + " = '" + id + "'", null);
    }
}
