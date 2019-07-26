package com.example.MovieCatalogue.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.MovieCatalogue.MovieDetailActivity;
import com.example.MovieCatalogue.PlainOldJavaObject.Movie;
import com.example.MovieCatalogue.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Movie> listMovie = new ArrayList<>();

    public MovieAdapter(Context context) {
        this.context = context;
    }

    private ArrayList<Movie> getListMovie() {
        return listMovie;
    }

    public void setListMovie(ArrayList<Movie> itemList) {
        listMovie.clear();
        listMovie.addAll(itemList);
        Collections.sort(listMovie);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_movie
                , viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Movie movie = getListMovie().get(i);
        viewHolder.tvTitle.setText(movie.getOriginalTitle());
        viewHolder.tvReleaseDate.setText(movie.getReleaseDate());
        viewHolder.tvOverview.setText(movie.getOverview());

        Glide.with(context)
                .load(Movie.PATH_IMG + movie.getPosterPath())
                .apply(new RequestOptions().fitCenter())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25,3)))
                .into(viewHolder.imgPoster);
        Glide.with(context)
                .load(Movie.SMALL_IMG + movie.getPosterPath())
                .apply(new RequestOptions().centerCrop())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3)))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        viewHolder.rvLayout.setBackground(resource);
                    }
                });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.TAG_DETAIL_MOVIE, movie);
                Pair<View, String> p1 = Pair.create((View) viewHolder.imgPoster, "poster");
                Pair<View, String> p2 = Pair.create((View) viewHolder.tvTitle, "title");
                Pair<View, String> p3 = Pair.create((View) viewHolder.tvReleaseDate, "release_date");
                Pair<View, String> p4 = Pair.create((View) viewHolder.tvOverview, "overview");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
                context.startActivity(intent,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMovie.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvReleaseDate, tvOverview;
        ImageView imgPoster;
        RelativeLayout rvLayout;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvLayout = itemView.findViewById(R.id.rv_layout);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvReleaseDate = itemView.findViewById(R.id.tv_item_date);
            tvOverview = itemView.findViewById(R.id.tv_item_overview);
            imgPoster = itemView.findViewById(R.id.img_item_poster);
        }
    }
}
