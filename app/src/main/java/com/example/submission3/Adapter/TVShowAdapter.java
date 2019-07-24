package com.example.submission3.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.submission3.PlainOldJavaObject.Movie;
import com.example.submission3.PlainOldJavaObject.TVShow;
import com.example.submission3.R;
import com.example.submission3.TVDetailActivity;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.ViewHolder> {
    private ArrayList<TVShow> tvShowArrayList = new ArrayList<>();
    private Context context;

    public TVShowAdapter(Context context) {
        this.context = context;
    }

    private ArrayList<TVShow> getTvShowArrayList() {
        return tvShowArrayList;
    }

    public void setTvShowList(ArrayList<TVShow> tvShowArrayList) {
        this.tvShowArrayList.clear();
        this.tvShowArrayList.addAll(tvShowArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid_tv,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final TVShow tvShow = getTvShowArrayList().get(i);
        viewHolder.tvTitleTv.setText(tvShow.getName());
        Glide.with(context)
                .load(Movie.PATH_IMG + tvShow.getPosterPath())
                .apply(new RequestOptions().fitCenter())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25,3)))
                .into(viewHolder.imgPosterTv);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TVDetailActivity.class);
                intent.putExtra(TVDetailActivity.TAG_DETAIL_TV, tvShow);
                Pair<View, String> p1 = Pair.create((View) viewHolder.imgPosterTv, "poster");
                Pair<View, String> p2 = Pair.create((View) viewHolder.tvTitleTv, "title");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, p1, p2);
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getTvShowArrayList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitleTv;
        ImageView imgPosterTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleTv = itemView.findViewById(R.id.tv_item_title);
            imgPosterTv = itemView.findViewById(R.id.img_item_poster);
        }
    }
}
