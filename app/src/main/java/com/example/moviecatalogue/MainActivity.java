package com.example.moviecatalogue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moviecatalogue.database.DatabaseContract;
import com.example.moviecatalogue.fragment.FavouriteFragment;
import com.example.moviecatalogue.fragment.MovieFragment;
import com.example.moviecatalogue.fragment.TVShowFragment;
import com.example.moviecatalogue.model.Movie;
import com.example.moviecatalogue.model.TVShow;
import com.example.moviecatalogue.viewModel.MovieViewModel;
import com.example.moviecatalogue.viewModel.TVShowViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.transition.TransitionManager.beginDelayedTransition;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //state penyimpanan bundle
    public static final String TAG_LIST_MOVIES = "tag_list_movies";
    public static final String TAG_LIST_TV = "tag_list_tv";
    public static final String TAG_LIST_FAV_MOVIE = "tag_list_fav_movie";
    public static final String TAG_LIST_FAV_TV = "tag_list_fav_tv";
    public static final String TAG_LANG = "tag_lang";
    public static final String TAG_STATE_MENU = "tag_state_menu";
    public static final String TAG_STATE_NAV_DRAWER = "tag_state_nav_drawer";
    //data untuk ditampilkan pada UI
    ArrayList<Movie> listMovies = new ArrayList<>();
    ArrayList<TVShow> listTVShows = new ArrayList<>();
    ArrayList<Movie> listReleasedNow = new ArrayList<>();
    ArrayList<Movie> listFavouriteMovies = new ArrayList<>();
    ArrayList<TVShow> listFavouriteTV = new ArrayList<>();
    //View Model Arch Lifecycle
    MovieViewModel movieViewModel;
    TVShowViewModel tvShowViewModel;
    Calendar calendar;
    //value untuk menyimpan state konfig bahasa
    private String CONFIG_LOCALE = "";
    //komponen fragment
    private MovieFragment movieFragment;
    private MovieFragment nowReleaseFragment;
    private TVShowFragment tvShowFragment;
    private FavouriteFragment favouriteFragment;
    //komponen main activity
    private BottomNavigationView bottomNavView;
    private ProgressBar progressBar;
    private ConstraintLayout rootContainer;
    private ConstraintSet containerSet;
    private LinearLayout searchLayout;
    private SearchView searchView;
    private LinearLayout.LayoutParams widthMatchParent;
    private LinearLayout.LayoutParams widthWrapContent;
    private TextView searchHint;
    private boolean doingSearchMovie = false;
    private boolean doingSearchTV = false;
    private int stateMenu = R.id.movie_nav;
    private int stateNavigationDrawer = R.id.nav_home;
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

    private Observer<ArrayList<Movie>> getReleasedNow = new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(ArrayList<Movie> movies) {
            if (movies != null) {
                //set to adapter
                listReleasedNow.clear();
                listReleasedNow.addAll(movies);
                if (nowReleaseFragment != null) {
                    nowReleaseFragment.setListMovies(listReleasedNow);
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

    private void hideSearchLayout() {
        containerSet.clone(rootContainer);
        containerSet.connect(R.id.container_fragment, ConstraintSet.TOP, R.id.root_container, ConstraintSet.TOP, 16);
        beginDelayedTransition(rootContainer);
        containerSet.applyTo(rootContainer);
        searchLayout.setVisibility(View.INVISIBLE);
    }

    private void pushFragmentLayoutToParent() {
        hideSearchLayout();
        bottomNavView.setVisibility(View.INVISIBLE);
        containerSet.clone(rootContainer);
        containerSet.connect(R.id.container_fragment, ConstraintSet.BOTTOM, R.id.root_container, ConstraintSet.BOTTOM, 30);
        beginDelayedTransition(rootContainer);
        containerSet.applyTo(rootContainer);
    }

    private void pushFragmentLayoutToBottomNav() {
        showSearchLayout();
        containerSet.clone(rootContainer);
        beginDelayedTransition(rootContainer);
        bottomNavView.setVisibility(View.VISIBLE);
        bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        bottomNavView.setSelectedItemId(stateMenu);
        containerSet.connect(R.id.container_fragment, ConstraintSet.BOTTOM, R.id.navigation, ConstraintSet.TOP, 30);
        containerSet.applyTo(rootContainer);
    }

    private void showSearchLayout() {
        containerSet.clone(rootContainer);
        containerSet.connect(R.id.container_fragment, ConstraintSet.TOP, R.id.search_layout, ConstraintSet.BOTTOM, 16);
        beginDelayedTransition(rootContainer);
        containerSet.applyTo(rootContainer);
        searchLayout.setVisibility(View.VISIBLE);
    }

    private void setSearchViewSetting() {
        searchHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHint.setVisibility(View.INVISIBLE);
                searchView.setLayoutParams(widthMatchParent);
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setLayoutParams(widthWrapContent);
                searchHint.setVisibility(View.VISIBLE);
                switch (stateMenu) {
                    case R.id.movie_nav:
                        if (doingSearchMovie) {
                            movieViewModel.setMovie(getApplication());
                            doingSearchMovie = false;
                        }
                        break;
                    case R.id.tv_show_nav:
                        if (doingSearchTV) {
                            tvShowViewModel.setTVShows(getApplication());
                            doingSearchTV = false;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHint.setVisibility(View.INVISIBLE);
                searchView.setLayoutParams(widthMatchParent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                if (!query.isEmpty()) {
                    switch (stateMenu) {
                        case R.id.movie_nav:
                            movieViewModel.setMovie(query, getApplication());
                            showLoading(true);
                            doingSearchMovie = true;
                            break;
                        case R.id.tv_show_nav:
                            tvShowViewModel.setTVShows(query, getApplication());
                            showLoading(true);
                            doingSearchTV = true;
                            break;
                        default:
                            Toast.makeText(getApplication(), getString(R.string.search_not_available), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        widthMatchParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        widthWrapContent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomNavView = findViewById(R.id.navigation);
        progressBar = findViewById(R.id.progress_bar);
        rootContainer = findViewById(R.id.root_container);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_view);
        searchHint = findViewById(R.id.search_hint);
        setSearchViewSetting();
        containerSet = new ConstraintSet();
        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getMovies().observe(this, getMovies);
        tvShowViewModel = ViewModelProviders.of(this).get(TVShowViewModel.class);
        tvShowViewModel.getTVShows().observe(this, getTVShows);
        movieViewModel.getNowReleasedMovies().observe(this, getReleasedNow);
        CONFIG_LOCALE = Locale.getDefault().getLanguage();
        stateNavigationDrawer = getIntent().getIntExtra(TAG_STATE_NAV_DRAWER,R.id.nav_home);
        doShowCase();
        if (savedInstanceState == null) {
            reloadFavouriteData();
            createDataTVShows();
            createDataMovie();
            createDataReleasedNow();
        } else {
            listMovies = savedInstanceState.getParcelableArrayList(TAG_LIST_MOVIES);
            listTVShows = savedInstanceState.getParcelableArrayList(TAG_LIST_TV);
            listFavouriteMovies = savedInstanceState.getParcelableArrayList(TAG_LIST_FAV_MOVIE);
            listFavouriteTV = savedInstanceState.getParcelableArrayList(TAG_LIST_FAV_TV);
            stateMenu = savedInstanceState.getInt(TAG_STATE_MENU);
            stateNavigationDrawer = savedInstanceState.getInt(TAG_STATE_NAV_DRAWER);
        }
        navigationView.setCheckedItem(stateNavigationDrawer);
        switch (stateNavigationDrawer){
            case R.id.nav_home:
                pushFragmentLayoutToBottomNav();
                break;
            case R.id.nav_released_now:
                pushFragmentLayoutToParent();
                showNowReleasedFragment();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (id == R.id.nav_home) {
            pushFragmentLayoutToBottomNav();
            drawer.closeDrawer(GravityCompat.START);
            stateNavigationDrawer = id;
            return true;
        }
        else if(id == R.id.nav_released_now){
            pushFragmentLayoutToParent();
            showNowReleasedFragment();
            drawer.closeDrawer(GravityCompat.START);
            stateNavigationDrawer = id;
            return true;
        }
        else if (id == R.id.nav_tools) {
            intent = new Intent(this, ReminderActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_app));
            intent.setType("text/plain");
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return false;
    }

    private void doShowCase(){
        ShowcaseConfig showcaseConfig = new ShowcaseConfig();
        showcaseConfig.setDelay(500);
        showcaseConfig.setDismissTextColor(getResources().getColor(R.color.colorAccentSecondary));
        MaterialShowcaseSequence showcaseSequence = new MaterialShowcaseSequence(this,getPackageName());
        showcaseSequence.setConfig(showcaseConfig);
        showcaseSequence.addSequenceItem(bottomNavView,getResources().getString(R.string.showcase_bottom_nav),"GOT IT");
        showcaseSequence.addSequenceItem(toolbar,getResources().getString(R.string.showcase_toolbar),"GOT IT");
        showcaseSequence.addSequenceItem(searchView,getResources().getString(R.string.showcase_search),"GOT IT");
        showcaseSequence.addSequenceItem(findViewById(R.id.example_showcase),getResources().getString(R.string.showcase_container_fragment),"GOT IT");
        showcaseSequence.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_preference), Context.MODE_PRIVATE);
        boolean isFavouriteChange = sharedPref.getBoolean(getString(R.string.key_favourite), false);
        if (isFavouriteChange) {
            reloadFavouriteData();
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
        bottomNavView.setSelectedItemId(stateMenu);
    }

    public void reloadFavouriteData() {
        listFavouriteMovies.clear();
        listFavouriteMovies.addAll(getFavMovieFromContentProvider());
        listFavouriteTV.clear();
        listFavouriteTV.addAll(getFavTVShowFromContentProvider());
        if (favouriteFragment != null) {
            favouriteFragment.setListFavouriteMovies(listFavouriteMovies);
            favouriteFragment.setListFavouriteTV(listFavouriteTV);
        }
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
        outState.putInt(TAG_STATE_NAV_DRAWER, stateNavigationDrawer);
    }

    private void showMovieFragment() {
        if (movieFragment == null) {
            movieFragment = new MovieFragment();
            movieFragment.setContext(this);
            movieFragment.setListMovies(listMovies);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment, movieFragment,
                        movieFragment.getClass().getSimpleName() + "FavouriteMovies")
                .commit();
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(getResources().getString(R.string.bar_title_movie));
    }

    private void showNowReleasedFragment() {
        if (nowReleaseFragment == null) {
            nowReleaseFragment = new MovieFragment();
            nowReleaseFragment.setContext(this);
            nowReleaseFragment.setListMovies(listReleasedNow);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment, nowReleaseFragment,
                        nowReleaseFragment.getClass().getSimpleName() + "ReleasedNow")
                .commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.menu_released_now));
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
                .replace(R.id.container_fragment, tvShowFragment,
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
                .replace(R.id.container_fragment, favouriteFragment, favouriteFragment.getClass().getSimpleName())
                .commit();
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(getResources().getString(R.string.bar_title_favourite_movie));
    }

    public void createDataMovie() {
        movieViewModel.setMovie(this);
        showLoading(true);
    }

    public void createDataReleasedNow() {
        calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(System.currentTimeMillis());
        String currentDate = DateFormat.format("yyyy-MM-dd", calendar).toString();
        movieViewModel.setMovieReleasedNow(this, currentDate);
        showLoading(true);
    }

    private ArrayList<Movie> getFavMovieFromContentProvider() {
        Cursor cursor = getContentResolver().query(
                DatabaseContract.MovieColumns.CONTENT_URI_MOVIE
                , null, null, null, null, null);
        ArrayList<Movie> listFromContentProvider = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Movie movie;
            do {
                movie = new Movie(cursor);
                listFromContentProvider.add(movie);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return listFromContentProvider;
    }

    private ArrayList<TVShow> getFavTVShowFromContentProvider() {
        Cursor cursor = getContentResolver().query(
                DatabaseContract.TVShowColumns.CONTENT_URI_TV
                , null, null, null, null, null);
        ArrayList<TVShow> listFromContentProvider = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            TVShow tvShow;
            do {
                tvShow = new TVShow(cursor);
                listFromContentProvider.add(tvShow);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return listFromContentProvider;
    }

    public void createDataTVShows() {
        tvShowViewModel.setTVShows(this);
        showLoading(true);
    }
}
