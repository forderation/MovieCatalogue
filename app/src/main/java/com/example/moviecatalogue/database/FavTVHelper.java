package com.example.moviecatalogue.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.moviecatalogue.database.DatabaseContract.TVShowColumns.*;

public class FavTVHelper {
    private static final String TABLE_TV = TABLE_TV_SHOW;
    private static DatabaseHelper dataBaseHelper;
    private static SQLiteDatabase database;

    private FavTVHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static FavTVHelper getInstance(Context context){
        return new FavTVHelper(context);
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
                TABLE_TV
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
                TABLE_TV
                ,null
                ,null
                ,null
                ,null
                ,null
                ,_ID + " ASC"
        );
    }

    public long insertProvider(ContentValues values){
        return database.insert(TABLE_TV,null,values);
    }

    public int updateProvider(String id, ContentValues values){
        return database.update(TABLE_TV,values,_ID + " = ?", new String[]{id});
    }

    public int deleteProvider(String id){
        return database.delete(TABLE_TV,idJSON + " = ?",new String[]{id});
    }
}
