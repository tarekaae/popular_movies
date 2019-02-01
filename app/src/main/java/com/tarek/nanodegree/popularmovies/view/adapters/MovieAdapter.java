package com.tarek.nanodegree.popularmovies.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.tarek.nanodegree.popularmovies.R;
import com.tarek.nanodegree.popularmovies.model.backend.API;
import com.tarek.nanodegree.popularmovies.model.pojo.Result;

import java.util.ArrayList;

/**
 * Created by tarek.abdulkader on 10/25/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private ArrayList<Result> dataSet;
    Picasso picasso;


    private OnItemClicked onClick;

    public MovieAdapter(Context mContext, ArrayList<Result> dataSet) {
        this.mContext = mContext;
        this.dataSet = dataSet;
        picasso = Picasso.with(mContext);
    }

    public void updateDataSet(ArrayList<Result> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        ProgressBar progressBar;

        MovieViewHolder(View view) {
            super(view);

            poster = (ImageView) view.findViewById(R.id.poster);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(v);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {

        Result result = dataSet.get(position);
        String posterPath = result.getPosterPath();
        String fullPath = API.IMAGES_BASE_URL + API.ORIGINAL_SIZE + posterPath;
        picasso.load(fullPath).into(holder.poster, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

                holder.poster.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.poster.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }
        });


        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}