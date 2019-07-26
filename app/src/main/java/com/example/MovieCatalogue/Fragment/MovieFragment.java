package com.example.MovieCatalogue.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MovieCatalogue.Adapter.MovieAdapter;
import com.example.MovieCatalogue.PlainOldJavaObject.Movie;
import com.example.MovieCatalogue.R;

import java.util.ArrayList;

public class MovieFragment extends Fragment {
    Context context;
    MovieAdapter movieAdapter;
    RecyclerView recyclerView;

    public MovieFragment() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    public void setListMovies(ArrayList<Movie> movies) {
        if (movieAdapter == null) {
            movieAdapter = new MovieAdapter(context);
        }
        movieAdapter.setListMovie(movies);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_movie);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(movieAdapter);
    }
}
