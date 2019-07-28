package com.example.moviecatalogue.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import static com.example.moviecatalogue.database.DatabaseContract.*;
import static com.example.moviecatalogue.database.DatabaseContract.MovieColumns.CONTENT_URI_MOVIE;
import static com.example.moviecatalogue.database.DatabaseContract.TVShowColumns.CONTENT_URI_TV;

import com.example.moviecatalogue.database.DatabaseContract;
import com.example.moviecatalogue.database.FavMovieHelper;
import com.example.moviecatalogue.database.FavTVHelper;


public class FavMovieProvider extends ContentProvider {
    private static final int TABLE_MOVIE = 1;
    private static final int TABLE_MOVIE_WITH_ID = 2;
    private static final int TABLE_TV = 3;
    private static final int TABLE_TV_WITH_ID = 4;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private FavMovieHelper favMovieHelper;
    private FavTVHelper favTVHelper;
    static {
        sUriMatcher.addURI(AUTHORITY,MovieColumns.TABLE_MOVIE, TABLE_MOVIE);
        sUriMatcher.addURI(AUTHORITY,MovieColumns.TABLE_MOVIE+"/#", TABLE_MOVIE_WITH_ID);
        sUriMatcher.addURI(AUTHORITY, DatabaseContract.TVShowColumns.TABLE_TV_SHOW, TABLE_TV);
        sUriMatcher.addURI(AUTHORITY, DatabaseContract.TVShowColumns.TABLE_TV_SHOW+"/#", TABLE_TV_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        favMovieHelper = FavMovieHelper.getInstance(getContext());
        favTVHelper = FavTVHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        favMovieHelper.open();
        favTVHelper.open();
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case TABLE_MOVIE:
                cursor = favMovieHelper.queryProvider();
                break;
            case TABLE_MOVIE_WITH_ID:
                cursor = favMovieHelper.queryByIdProvider(""+uri.getLastPathSegment());
                break;
            case TABLE_TV:
                cursor = favTVHelper.queryProvider();
                break;
            case TABLE_TV_WITH_ID:
                cursor = favTVHelper.queryByIdProvider(""+uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        favMovieHelper.open();
        favTVHelper.open();
        long added;
        final int codeUri = sUriMatcher.match(uri);
        switch (codeUri){
            case TABLE_MOVIE:
                added = favMovieHelper.insertProvider(values);
                break;
            case TABLE_TV:
                added = favTVHelper.insertProvider(values);
                break;
            default:
                added = 0;
                break;
        }
        Context context = getContext();
        if(codeUri==TABLE_MOVIE){
            if(context!=null){
                context.getContentResolver().notifyChange(CONTENT_URI_MOVIE, null);
            }
            return Uri.parse(CONTENT_URI_MOVIE +"/"+added);
        }else if(codeUri==TABLE_TV){
            if(context!=null){
                context.getContentResolver().notifyChange(CONTENT_URI_TV, null);
            }
            return Uri.parse(CONTENT_URI_TV + "/" + added);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        favMovieHelper.open();
        favTVHelper.open();
        int deleted;
        final int codeUri = sUriMatcher.match(uri);
        switch (codeUri){
            case TABLE_MOVIE_WITH_ID:
                deleted = favMovieHelper.deleteProvider(uri.getLastPathSegment());
                break;
            case TABLE_TV_WITH_ID:
                deleted = favTVHelper.deleteProvider(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }
        Context context = getContext();
        if(codeUri==TABLE_MOVIE_WITH_ID){
            if(context!=null){
                context.getContentResolver().notifyChange(CONTENT_URI_MOVIE, null);
            }
        }else if(codeUri==TABLE_TV_WITH_ID){
            if(context!=null){
                context.getContentResolver().notifyChange(CONTENT_URI_TV, null);
            }
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
//        favMovieHelper.open();
//        int updated;
//        if (sUriMatcher.match(uri) == TABLE_MOVIE_WITH_ID) {
//            updated = favMovieHelper.updateProvider(uri.getLastPathSegment(), values);
//        } else {
//            updated = 0;
//        }
//        Context context = getContext();
//        if(context!=null){
//            context.getContentResolver().notifyChange(CONTENT_URI_MOVIE, null);
//        }
        return 0;
    }
}
