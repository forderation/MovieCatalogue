package com.example.moviecatalogue.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.moviecatalogue.database.DatabaseContract.MovieColumns.*;

public class FavMovieHelper {
    private static final String TABLE_MOVIE = DatabaseContract.MovieColumns.TABLE_MOVIE;
    private static DatabaseHelper dataBaseHelper;
    private static SQLiteDatabase database;

    private FavMovieHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static FavMovieHelper getInstance(Context context){
        return new FavMovieHelper(context);
    }

    public void open() throws SQLException {
      if(database==null){
          database = dataBaseHelper.getWritableDatabase();
      }
      if(!database.isOpen()){
          database = dataBaseHelper.getWritableDatabase();
      }
    }

    public Cursor queryByIdProvider(String id){
        Log.d("query","query id : " + id);
        return database.query(
                TABLE_MOVIE
                ,null
                , idJSON + " = '"+ id +"'"
                ,null
                ,null
                ,null
                ,null
                ,null
                );
    }

    public Cursor queryProvider(){
        return database.query(
                TABLE_MOVIE
                ,null
                ,null
                ,null
                ,null
                ,null
                ,_ID + " ASC"
        );
    }

    public long insertProvider(ContentValues values){
        return database.insert(TABLE_MOVIE,null,values);
    }

    public int updateProvider(String id, ContentValues values){
        return database.update(TABLE_MOVIE,values,_ID + " = ?", new String[]{id});
    }

    public int deleteProvider(String id){
        return database.delete(TABLE_MOVIE,idJSON + " = ?",new String[]{id});
    }
}
