package com.example.MovieCatalogue.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MovieCatalogue.PlainOldJavaObject.TVShow;
import com.example.MovieCatalogue.R;
import com.example.MovieCatalogue.Adapter.TVShowAdapter;

import java.util.ArrayList;


public class TVShowFragment extends Fragment {
    TVShowAdapter tvShowAdapter;
    Context context;
    RecyclerView recyclerView;

    public TVShowFragment() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListTVShows(ArrayList<TVShow> tvShows) {
        if (tvShowAdapter == null) {
            tvShowAdapter = new TVShowAdapter(context);
        }
        tvShowAdapter.setTvShowList(tvShows);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_tv_show);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(tvShowAdapter);
    }
}
