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
import com.example.MovieCatalogue.PlainOldJavaObject.TVShow;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TVDetailActivity extends AppCompatActivity {
    public static final String TAG_DETAIL_TV = "tag_detail_tv";
    boolean isActionTrash = false;
    FloatingActionButton fab;
    private Menu menu;
    private FavouriteHelper favouriteHelper;
    private SharedPreferences sharedPref;
    private TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);
        this.tvShow = getIntent().getParcelableExtra(TAG_DETAIL_TV);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvName, tvOriginalName, tvVoteAverage, tvOverview, tvFirstAirDate, tvOriLang;
        ImageView imgPoster, imgBackground;
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        tvName = findViewById(R.id.tv_title);
        tvOriginalName = findViewById(R.id.tv_original_name);
        tvVoteAverage = findViewById(R.id.tv_vote);
        tvOverview = findViewById(R.id.tv_overview);
        fab = findViewById(R.id.fab);
        tvFirstAirDate = findViewById(R.id.tv_first_air_date);
        tvOriLang = findViewById(R.id.tv_original_language);
        imgPoster = findViewById(R.id.image_poster);
        imgBackground = findViewById(R.id.expanded_image);
        favouriteHelper = FavouriteHelper.getInstance(getApplicationContext());
        favouriteHelper.open();
        if (tvShow != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(tvShow.getName());
            tvName.setText(tvShow.getName());
            tvOriginalName.setText(tvShow.getOriginalName());
            tvVoteAverage.setText(tvShow.getVoteAverage());
            tvOverview.setText(tvShow.getOverview());
            tvFirstAirDate.setText(tvShow.getFirstAirDate());
            tvOriLang.setText(tvShow.getOriginalLanguage());
            Glide.with(this)
                    .load(Movie.PATH_IMG + tvShow.getPosterPath())
                    .apply(new RequestOptions().fitCenter())
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25, 3)))
                    .into(imgPoster);
            Glide.with(this)
                    .load(Movie.PATH_IMG + tvShow.getBackdropPath())
                    .apply(new RequestOptions().centerCrop())
                    .into(imgBackground);
            if (favouriteHelper.isTVShowFavourites(tvShow.getId())) {
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

    public void changeStatusFavourite() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.key_favourite), true);
        editor.apply();
        if (isActionTrash) {
            long result = favouriteHelper.deleteFavourite(tvShow.getId());
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
            long result = favouriteHelper.insert(tvShow);
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
}
