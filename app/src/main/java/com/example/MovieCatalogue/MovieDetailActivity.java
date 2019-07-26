package com.example.MovieCatalogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.MovieCatalogue.Database.FavouriteHelper;
import com.example.MovieCatalogue.PlainOldJavaObject.Movie;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String TAG_DETAIL_MOVIE = MovieDetailActivity.class.getSimpleName();
    boolean isActionTrash = false;
    FloatingActionButton fab;
    private Menu menu;
    private Movie movie;
    private FavouriteHelper favouriteHelper;
    private SharedPreferences sharedPref;

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
        favouriteHelper = FavouriteHelper.getInstance(getApplicationContext());
        favouriteHelper.open();
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
            if (favouriteHelper.isMovieFavourites(movie.getId())) {
                isActionTrash = true;
                fab.setImageResource(R.drawable.ic_delete_black_24dp);
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
            long result = favouriteHelper.deleteFavourite(movie.getId());
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
            long result = favouriteHelper.insert(movie);
            if (result > 0) {
                isActionTrash = true;
                fab.setImageResource(R.drawable.ic_delete_black_24dp);
                MenuItem itemOption = menu.findItem(R.id.action_info);
                itemOption.setIcon(getDrawable(R.drawable.ic_delete_black_24dp));
                Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.succes_add_favourite), Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.fail_change_stat), Snackbar.LENGTH_SHORT)
                        .show();
            }
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
