package com.tarek.nanodegree.popularmovies.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tarek.nanodegree.popularmovies.R;
import com.tarek.nanodegree.popularmovies.model.pojo.Review;
import com.tarek.nanodegree.popularmovies.model.pojo.Trailer;

import java.util.ArrayList;

/**
 * Created by tarek.abdulkader on 1/8/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private Context context;
    private ArrayList<Review> dataSet;

    public ReviewsAdapter(Context context, ArrayList<Review> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        ReviewViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.reviewTitle);
            content = (TextView) view.findViewById(R.id.reviewContent);
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        ReviewViewHolder movieViewHolder = new ReviewViewHolder(v);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, final int position) {

        holder.title.setText(dataSet.get(position).getAuthor());
        holder.content.setText(dataSet.get(position).getContent());


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
