package com.example.MovieCatalogue.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MovieCatalogue.Adapter.SectionsPagerAdapter;
import com.example.MovieCatalogue.PlainOldJavaObject.Movie;
import com.example.MovieCatalogue.PlainOldJavaObject.TVShow;
import com.example.MovieCatalogue.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    private MovieFragment movieFavouriteFragment;
    private TVShowFragment tvShowFavouriteFragment;
    private Context context;
    public FavouriteFragment() {
        // Required empty public constructor
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListFavouriteMovies(ArrayList<Movie> listFavouriteMovies) {
        if (movieFavouriteFragment == null) {
            movieFavouriteFragment = new MovieFragment();
            movieFavouriteFragment.setContext(context);
        }
        movieFavouriteFragment.setListMovies(listFavouriteMovies);
    }

    public void setListFavouriteTV(ArrayList<TVShow> listFavouriteTV) {
        if(tvShowFavouriteFragment == null){
            tvShowFavouriteFragment = new TVShowFragment();
            tvShowFavouriteFragment.setContext(context);
        }
        tvShowFavouriteFragment.setListTVShows(listFavouriteTV);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("fragment","view created");
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        sectionsPagerAdapter.addFragment(movieFavouriteFragment, getString(R.string.movie_nav));
        sectionsPagerAdapter.addFragment(tvShowFavouriteFragment,getString(R.string.tv_show_nav));
        viewPager.setAdapter(sectionsPagerAdapter);
    }
}
