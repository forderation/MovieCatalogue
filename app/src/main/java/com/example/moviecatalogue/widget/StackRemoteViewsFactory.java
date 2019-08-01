package com.example.moviecatalogue.widget;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.moviecatalogue.MovieDetailActivity;
import com.example.moviecatalogue.R;
import com.example.moviecatalogue.database.DatabaseContract;
import com.example.moviecatalogue.model.Movie;
import java.util.concurrent.ExecutionException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor cursor;
    public StackRemoteViewsFactory(Context context){
        mContext = context;
    }
    @Override
    public void onCreate() {
         cursor = mContext.getContentResolver().query(
                DatabaseContract.MovieColumns.CONTENT_URI_MOVIE
                , null, null, null, null, null);
         Log.d("widgetlog","cursor size: "+cursor.getCount());
    }

    private Movie getFav(int position){
        if(!cursor.moveToPosition(position)){
            throw new IllegalStateException("position has not been found");
        }
        return new Movie(cursor);
    }

    @Override
    public void onDataSetChanged() {
        if(cursor!=null){
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        cursor = mContext.getContentResolver().query(
                DatabaseContract.MovieColumns.CONTENT_URI_MOVIE, null, null, null, null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if(cursor!=null){
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Movie movie = getFav(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.widget_item);
        Bitmap imgPoster;
        try {
            imgPoster = Glide.with(mContext)
                    .asBitmap()
                    .load(Movie.PATH_IMG+movie.getPosterPath())
                    .into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                    .get();
            rv.setImageViewBitmap(R.id.image_view_widget,imgPoster);
            rv.setTextViewText(R.id.tv_movie_title,movie.getOriginalTitle());
        }catch (InterruptedException | ExecutionException e){
            Log.d("widget","error load image");
        }
        Bundle extras = new Bundle();
        extras.putString(FavMovieWidget.TOAST_ACTION,movie.getReleaseDate());
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.image_view_widget,fillIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return cursor.moveToPosition(position)? cursor.getLong(0):position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
