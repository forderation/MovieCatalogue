package com.example.MovieCatalogue;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.MovieCatalogue.CustomView.CustomButton;
import com.example.MovieCatalogue.CustomView.CustomEdtText;
import com.example.MovieCatalogue.Database.FavouriteHelper;
import com.example.MovieCatalogue.Fragment.FavouriteFragment;
import com.example.MovieCatalogue.Fragment.MovieFragment;
import com.example.MovieCatalogue.Fragment.TVShowFragment;
import com.example.MovieCatalogue.PlainOldJavaObject.Movie;
import com.example.MovieCatalogue.PlainOldJavaObject.TVShow;
import com.example.MovieCatalogue.ViewModel.MovieViewModel;
import com.example.MovieCatalogue.ViewModel.TVShowViewModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //state penyimpanan bundle
    public static final String TAG_LIST_MOVIES = "tag_list_movies";
    public static final String TAG_LIST_TV = "tag_list_tv";
    public static final String TAG_LIST_FAV_MOVIE = "tag_list_fav_movie";
    public static final String TAG_LIST_FAV_TV = "tag_list_fav_tv";
    public static final String TAG_LANG = "tag_lang";
    public static final String TAG_STATE_MENU = "tag_state_menu";
    //data untuk ditampilkan pada UI
    ArrayList<Movie> listMovies = new ArrayList<>();
    ArrayList<Movie> listFavouriteMovies = new ArrayList<>();
    ArrayList<Long> listIdFavMovie = new ArrayList<>();
    ArrayList<TVShow> listTVShows = new ArrayList<>();
    ArrayList<TVShow> listFavouriteTV = new ArrayList<>();
    ArrayList<Long> listIdFavTV = new ArrayList<>();
    //View Model Arch Lifecycle
    MovieViewModel movieViewModel;
    TVShowViewModel tvShowViewModel;
    //database helper
    FavouriteHelper favouriteHelper;
    //value untuk menyimpan state konfig bahasa
    private String CONFIG_LOCALE = "";
    //komponen fragment
    private MovieFragment movieFragment;
    private TVShowFragment tvShowFragment;
    private FavouriteFragment favouriteFragment;
    //komponen main activity
    private BottomNavigationView navigationView;
    private ProgressBar progressBar;
    private CustomButton customButton;
    private CustomEdtText customEdtText;
    private ConstraintLayout container;
    private ConstraintSet containerSet;
    private LinearLayout searchLayout;
    private int stateMenu = R.id.movie_nav;
    /*
     * @method_for = implementasi listener bottom navigasi
     * */
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.movie_nav:
                    showSearchLayout();
                    showMovieFragment();
                    stateMenu = R.id.movie_nav;
                    return true;
                case R.id.tv_show_nav:
                    showSearchLayout();
                    showTVShowFragment();
                    stateMenu = R.id.tv_show_nav;
                    return true;
                case R.id.favourite_nav:
                    hideSearchLayout();
                    showFavouriteFragment();
                    stateMenu = R.id.favourite_nav;
                    return true;
            }
            return false;
        }
    };

    private void hideSearchLayout(){
        containerSet.clone(container);
        containerSet.connect(R.id.container_layout,ConstraintSet.TOP,R.id.container,ConstraintSet.TOP,16);
        containerSet.applyTo(container);
        searchLayout.setVisibility(View.INVISIBLE);
    }

    private void showSearchLayout(){
        containerSet.clone(container);
        containerSet.connect(R.id.container_layout,ConstraintSet.TOP,R.id.search_layout,ConstraintSet.BOTTOM,16);
        containerSet.applyTo(container);
        searchLayout.setVisibility(View.VISIBLE);
    }

    private Observer<ArrayList<Movie>> getMovies = new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(ArrayList<Movie> movie) {
            if (movie != null) {
                //set to adapter
                listMovies.clear();
                listMovies.addAll(movie);
                if (movieFragment != null) {
                    movieFragment.setListMovies(listMovies);
                }
                showLoading(false);
            }
        }
    };

    private Observer<ArrayList<TVShow>> getTVShows = new Observer<ArrayList<TVShow>>() {
        @Override
        public void onChanged(ArrayList<TVShow> tvShows) {
            if (tvShows != null) {
                //set to adapter
                listTVShows.clear();
                listTVShows.addAll(tvShows);
                if (tvShowFragment != null) {
                    tvShowFragment.setListTVShows(listTVShows);
                }
                showLoading(false);
            }
        }
    };

    private Observer<ArrayList<Movie>> getFavouritesMovies = new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(@Nullable ArrayList<Movie> movies) {
            if (movies != null) {
                listFavouriteMovies.clear();
                listFavouriteMovies.addAll(movies);
                if (favouriteFragment != null) {
                    favouriteFragment.setListFavouriteMovies(listFavouriteMovies);
                }
                showLoading(false);
            }
        }
    };

    private Observer<ArrayList<TVShow>> getFavouritesTV = new Observer<ArrayList<TVShow>>() {
        @Override
        public void onChanged(@Nullable ArrayList<TVShow> tvShows) {
            if (tvShows != null) {
                listFavouriteTV.clear();
                listFavouriteTV.addAll(tvShows);
                if (favouriteFragment != null) {
                    favouriteFragment.setListFavouriteTV(listFavouriteTV);
                }
                showLoading(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigation);
        progressBar = findViewById(R.id.progress_bar);
        customButton = findViewById(R.id.search_button);
        customEdtText = findViewById(R.id.edit_text);
        container = findViewById(R.id.container);
        searchLayout = findViewById(R.id.search_layout);
        containerSet = new ConstraintSet();
        setMyButtonEnable();
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getMovies().observe(this, getMovies);
        movieViewModel.getMoviesFavourites().observe(this, getFavouritesMovies);

        tvShowViewModel = ViewModelProviders.of(this).get(TVShowViewModel.class);
        tvShowViewModel.getTVShowsFavourite().observe(this, getFavouritesTV);
        tvShowViewModel.getTVShows().observe(this, getTVShows);
        CONFIG_LOCALE = Locale.getDefault().getLanguage();
        reloadFavouriteData();
        if (savedInstanceState == null) {
            createDataTVShows();
            createDataMovie();
            createDataFavourite();
        } else {
            listMovies = savedInstanceState.getParcelableArrayList(TAG_LIST_MOVIES);
            listTVShows = savedInstanceState.getParcelableArrayList(TAG_LIST_TV);
            listFavouriteMovies = savedInstanceState.getParcelableArrayList(TAG_LIST_FAV_MOVIE);
            listFavouriteTV = savedInstanceState.getParcelableArrayList(TAG_LIST_FAV_TV);
            stateMenu = savedInstanceState.getInt(TAG_STATE_MENU);
        }
        navigationView.setSelectedItemId(stateMenu);

        customEdtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setMyButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchResult = Objects.requireNonNull(customEdtText.getText()).toString();
                if (searchResult.isEmpty()) {
                    switch (stateMenu) {
                        case R.id.movie_nav:
                            movieViewModel.setMovie(getApplication());
                            break;
                        case R.id.tv_show_nav:
                            tvShowViewModel.setTVShows(getApplication());
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(customEdtText.getWindowToken(), 0);
                String query = Objects.requireNonNull(customEdtText.getText()).toString();
                if (!query.isEmpty()) {
                    switch (stateMenu) {
                        case R.id.movie_nav:
                            movieViewModel.setMovie(query, getApplication());
                            showLoading(true);
                            break;
                        case R.id.tv_show_nav:
                            tvShowViewModel.setTVShows(query, getApplication());
                            showLoading(true);
                            break;
                        default:
                            Toast.makeText(getApplication(), getString(R.string.search_not_available), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

            }
        });
    }

    private void setMyButtonEnable() {
        Editable result = customEdtText.getText();
        if (result != null && !result.toString().isEmpty()) {
            customButton.setEnabled(true);
        } else {
            customButton.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        favouriteHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_preference), Context.MODE_PRIVATE);
        boolean isFavouriteChange = sharedPref.getBoolean(getString(R.string.key_favourite), false);
        if (isFavouriteChange) {
            reloadFavouriteData();
            createDataFavourite();
            Snackbar.make(findViewById(android.R.id.content), R.string.update_db, Snackbar.LENGTH_SHORT)
                    .show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.key_favourite), false);
            editor.apply();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String checkLang = Locale.getDefault().getLanguage();
        if (savedInstanceState != null) {
            CONFIG_LOCALE = savedInstanceState.getString(TAG_LANG);
            stateMenu = savedInstanceState.getInt(TAG_STATE_MENU);
        }
        if (checkLang.compareTo(CONFIG_LOCALE) != 0) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        reloadFavouriteData();
        navigationView.setSelectedItemId(stateMenu);
    }

    public void reloadFavouriteData() {
        favouriteHelper = FavouriteHelper.getInstance(getApplicationContext());
        favouriteHelper.open();
        listIdFavMovie.clear();
        listIdFavMovie.addAll(favouriteHelper.getMovieFavourites());
        listIdFavTV.clear();
        listIdFavTV.addAll(favouriteHelper.getTVShowFavourite());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TAG_LIST_MOVIES, listMovies);
        outState.putParcelableArrayList(TAG_LIST_TV, listTVShows);
        outState.putParcelableArrayList(TAG_LIST_FAV_MOVIE, listFavouriteMovies);
        outState.putInt(TAG_STATE_MENU, stateMenu);
        outState.putParcelableArrayList(TAG_LIST_FAV_TV, listFavouriteTV);
        outState.putString(TAG_LANG, CONFIG_LOCALE);
    }

    private void showMovieFragment() {
        if (movieFragment == null) {
            movieFragment = new MovieFragment();
            movieFragment.setContext(this);
            movieFragment.setListMovies(listMovies);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_layout, movieFragment,
                        movieFragment.getClass().getSimpleName())
                .commit();
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(getResources().getString(R.string.bar_title_movie));
    }

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showTVShowFragment() {
        if (tvShowFragment == null) {
            tvShowFragment = new TVShowFragment();
            tvShowFragment.setContext(this);
            tvShowFragment.setListTVShows(listTVShows);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_layout, tvShowFragment,
                        tvShowFragment.getClass().getSimpleName())
                .commit();
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(getResources().getString(R.string.bar_title_tv));
    }

    private void showFavouriteFragment() {
        if (favouriteFragment == null) {
            favouriteFragment = new FavouriteFragment();
            favouriteFragment.setContext(this);
            favouriteFragment.setListFavouriteMovies(listFavouriteMovies);
            favouriteFragment.setListFavouriteTV(listFavouriteTV);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_layout, favouriteFragment, favouriteFragment.getClass().getSimpleName())
                .commit();
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(getResources().getString(R.string.bar_title_favourite_movie));
    }

    public void createDataMovie() {
        movieViewModel.setMovie(this);
        showLoading(true);
    }

    public void createDataFavourite() {
        movieViewModel.setFavouriteMovie(this, listIdFavMovie);
        tvShowViewModel.setFavouriteMovie(this, listIdFavTV);
        showLoading(true);
    }

    public void createDataTVShows() {
        tvShowViewModel.setTVShows(this);
        showLoading(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_settings) {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
            return true;
        }
        return false;
    }
}

