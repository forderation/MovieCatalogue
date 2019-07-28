package com.example.moviecatalogue;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviecatalogue.database.DatabaseContract;
import com.example.moviecatalogue.model.Movie;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.moviecatalogue.database.DatabaseContract.MovieColumns.CONTENT_URI_MOVIE;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String TAG_DETAIL_MOVIE = MovieDetailActivity.class.getSimpleName();
    boolean isActionTrash = false;
    FloatingActionButton fab;
    private Menu menu;
    private Movie movie;
    private SharedPreferences sharedPref;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle, tvOverview, tvReleaseDate, tvAdult,
                tvVoteAverage, tvLanguage;
        tvTitle = findViewById(R.id.tv_title);
        tvOverview = findViewById(R.id.tv_overview);
        tvReleaseDate = findViewById(R.id.tv_release_date);
        tvAdult = findViewById(R.id.tv_adult);
        tvVoteAverage = findViewById(R.id.tv_vote);
        tvLanguage = findViewById(R.id.tv_original_language);
        fab = findViewById(R.id.fab);
        ImageView imagePoster = findViewById(R.id.image_poster);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        movie = getIntent().getParcelableExtra(TAG_DETAIL_MOVIE);
        ImageView expandedImage = findViewById(R.id.expanded_image);
        if (movie != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(movie.getOriginalTitle());
            tvTitle.setText(movie.getOriginalTitle());
            tvOverview.setText(movie.getOverview());
            tvReleaseDate.setText(movie.getReleaseDate());
            if (movie.isAdult()) {
                tvAdult.setText("18+");
            } else {
                tvAdult.setText("0+");
            }
            tvVoteAverage.setText(movie.getVoteAverage());
            tvLanguage.setText(movie.getOriginalLanguage());
            Glide.with(getApplicationContext())
                    .load(Movie.PATH_IMG + movie.getPosterPath())
                    .apply(new RequestOptions().fitCenter())
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25, 3)))
                    .into(imagePoster);
            Glide.with(getApplicationContext())
                    .load(Movie.PATH_IMG + movie.getBackdropPath())
                    .apply(new RequestOptions().centerCrop())
                    .into(expandedImage);
            uri = Uri.parse(CONTENT_URI_MOVIE +"/"+movie.getId());
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            if (cursor != null && cursor.getCount()>0) {
                isActionTrash = true;
                fab.setImageResource(R.drawable.ic_delete_black_24dp);
                cursor.close();
            }
        }
        this.sharedPref = this.getSharedPreferences(getString(R.string.key_preference), Context.MODE_PRIVATE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatusFavourite();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0) {
                    isShow = true;
                    showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_info);
                }
            }
        });
    }

    public void changeStatusFavourite() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.key_favourite), true);
        editor.apply();
        if (isActionTrash) {
            int result = getContentResolver().delete(uri,null,null);
            if (result > 0) {
                isActionTrash = false;
                fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                MenuItem itemOption = menu.findItem(R.id.action_info);
                itemOption.setIcon(getDrawable(R.drawable.ic_favorite_black_24dp));
                Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.succes_remove_favourite), Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.fail_change_stat), Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.MovieColumns.idJSON,movie.getId());
            values.put(DatabaseContract.MovieColumns.adult,""+movie.isAdult());
            values.put(DatabaseContract.MovieColumns.backdrop_path,movie.getBackdropPath());
            values.put(DatabaseContract.MovieColumns.poster_path,movie.getPosterPath());
            values.put(DatabaseContract.MovieColumns.vote_average,movie.getVoteAverage());
            values.put(DatabaseContract.MovieColumns.release_date,movie.getReleaseDate());
            values.put(DatabaseContract.MovieColumns.original_language,movie.getOriginalLanguage());
            values.put(DatabaseContract.MovieColumns.original_title,movie.getOriginalTitle());
            values.put(DatabaseContract.MovieColumns.overview,movie.getOverview());
            getContentResolver().insert(CONTENT_URI_MOVIE,values);
            isActionTrash = true;
            fab.setImageResource(R.drawable.ic_delete_black_24dp);
            MenuItem itemOption = menu.findItem(R.id.action_info);
            itemOption.setIcon(getDrawable(R.drawable.ic_delete_black_24dp));
            Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.succes_add_favourite), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            changeStatusFavourite();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        if (isActionTrash) {
            MenuItem itemOption = menu.findItem(R.id.action_info);
            itemOption.setIcon(getDrawable(R.drawable.ic_delete_black_24dp));
        }
        hideOption(R.id.action_info);
        return super.onCreateOptionsMenu(menu);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }
}
