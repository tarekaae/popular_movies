package com.tarek.nanodegree.popularmovies.view;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tarek.nanodegree.popularmovies.R;
import com.tarek.nanodegree.popularmovies.model.backend.API;
import com.tarek.nanodegree.popularmovies.model.backend.MovieAPI;
import com.tarek.nanodegree.popularmovies.model.db.DatabaseHandler;
import com.tarek.nanodegree.popularmovies.model.pojo.MovieReviews;
import com.tarek.nanodegree.popularmovies.model.pojo.MovieTrailers;
import com.tarek.nanodegree.popularmovies.model.pojo.Result;
import com.tarek.nanodegree.popularmovies.model.pojo.Review;
import com.tarek.nanodegree.popularmovies.model.pojo.Trailer;
import com.tarek.nanodegree.popularmovies.model.pojo.TrailersAndReviews;
import com.tarek.nanodegree.popularmovies.model.providers.MovieContract;
import com.tarek.nanodegree.popularmovies.view.adapters.ReviewsAdapter;
import com.tarek.nanodegree.popularmovies.view.adapters.TrailersAdapter;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailsActivity extends AppCompatActivity implements TrailersAdapter.OnItemClicked {

    private TextView moviename;
    private ImageView moviePoster;
    private TextView movieYear;
    private TextView movieDuration;
    private TextView movieRating;
    private Button favBtn;
    private TextView movieDesc;
    private ProgressBar progressBar, progressBarTrailers, progressBarReviews;
    private ProgressDialog progressDialog;
    private Retrofit retrofit;
    // trailers
    private RecyclerView recyclerViewTrailers;
    private TextView trailersTitle;
    private ArrayList<Trailer> allTrailers;
    //reviews
    private RecyclerView recyclerViewReviews;
    private TextView reviewsTitle;
    private ArrayList<Review> allReviews;
    //
    private Result result;
    DatabaseHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        handler = new DatabaseHandler(this);

        favBtn = (Button) findViewById(R.id.btn_fav);
        movieDesc = (TextView) findViewById(R.id.movieDesc);
        movieRating = (TextView) findViewById(R.id.movieRating);
        movieDuration = (TextView) findViewById(R.id.movieDuration);
        movieYear = (TextView) findViewById(R.id.movieYear);
        moviePoster = (ImageView) findViewById(R.id.moviePoster);
        moviename = (TextView) findViewById(R.id.movie_name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // stage 2
        progressBarTrailers = (ProgressBar) findViewById(R.id.progressBarTrailers);
        trailersTitle = (TextView) findViewById(R.id.trailers);
        recyclerViewTrailers = (RecyclerView) findViewById(R.id.recyclerViewTrailers);
        //
        progressBarReviews = (ProgressBar) findViewById(R.id.progressBarReviews);
        reviewsTitle = (TextView) findViewById(R.id.reviews);
        recyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerViewReveies);


        String details = (String) getIntent().getExtras().get("details");
        Gson gson = new Gson();
        result = gson.fromJson(details, Result.class);
        drawFavourite();
        // progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        int movieId = -1;
        if (result != null) {
            movieId = result.getId();

            getMovieData(movieId);


        } else {
            Log.e("MovieDetailsActivity", "result is null");
        }


        moviename.setText(result.getOriginalTitle());
        movieDesc.setText(result.getOverview());
        movieDuration.setText(result.getReleaseDate());
        movieYear.setText(result.getReleaseDate().substring(0, 4));
        movieRating.setText(result.getVoteAverage() + " /10");

        String posterPath = result.getPosterPath();
        String fullPath = API.IMAGES_BASE_URL + "w92" + posterPath;
        Picasso.with(MovieDetailsActivity.this).load(fullPath).fit().into(moviePoster, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

                moviePoster.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                moviePoster.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }


    private void getMovieData(final int movieId) {

        progressDialog.show();
        retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final MovieAPI movieAPI = retrofit.create(MovieAPI.class);

        Call<MovieTrailers> call = movieAPI.getVideos(movieId, API.API_KEY_V3);

        call.enqueue(new Callback<MovieTrailers>() {
            @Override
            public void onResponse(Call<MovieTrailers> call, Response<MovieTrailers> response) {

                final MovieTrailers trailers = response.body();

                Call<MovieReviews> ReviewsCall = movieAPI.getReviews(movieId, API.API_KEY_V3);
                ReviewsCall.enqueue(new Callback<MovieReviews>() {
                    @Override
                    public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {

                        MovieReviews reviews = response.body();

                        drawData(new TrailersAndReviews(reviews, trailers));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<MovieReviews> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        Log.e("onFailure", t.toString());

                    }
                });
            }

            @Override
            public void onFailure(Call<MovieTrailers> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", t.toString());
            }
        });


    }

    private void drawData(TrailersAndReviews trailersAndReviews) {

        if (trailersAndReviews.getTrailers() != null) {
            if (trailersAndReviews.getTrailers().getResults() != null) {
                if (trailersAndReviews.getTrailers().getResults().size() > 0) {


                    trailersTitle.setVisibility(View.VISIBLE);
                    recyclerViewTrailers.setVisibility(View.VISIBLE);
                    allTrailers = (ArrayList<Trailer>) trailersAndReviews.getTrailers().getResults();
                    TrailersAdapter mRecyclerViewTrailersAdapter =
                            new TrailersAdapter(this, allTrailers);
                    recyclerViewTrailers.setAdapter(mRecyclerViewTrailersAdapter);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewTrailers.setLayoutManager(mLayoutManager);
                    mRecyclerViewTrailersAdapter.setOnClick(this); // Bind the listener

                } else {
                    displayToast("NO Trailers Attached");
                }
            } else {
                displayToast("NO Trailers Attached");
            }
        } else {
            displayToast("NO Trailers Attached");
        }
        progressBarTrailers.setVisibility(View.GONE);


        ///////////////


        if (trailersAndReviews.getReviews() != null) {
            if (trailersAndReviews.getReviews().getResults() != null) {
                if (trailersAndReviews.getReviews().getResults().size() > 0) {


                    reviewsTitle.setVisibility(View.VISIBLE);
                    recyclerViewReviews.setVisibility(View.VISIBLE);
                    allReviews = (ArrayList<Review>) trailersAndReviews.getReviews().getResults();
                    ReviewsAdapter mRecyclerViewReviewsAdapter =
                            new ReviewsAdapter(this, allReviews);
                    recyclerViewReviews.setAdapter(mRecyclerViewReviewsAdapter);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewReviews.setLayoutManager(mLayoutManager);

                } else {
                    displayToast("NO Reviews Attached");
                }
            } else {
                displayToast("NO Reviews Attached");
            }
        } else {
            displayToast("NO Reviews Attached");
        }
        progressBarReviews.setVisibility(View.GONE);


    }

    private void displayToast(String msg) {
        Toast.makeText(MovieDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(int position) {

        Trailer selectedTrailer = allTrailers.get(position);

        if (selectedTrailer.getSite().equalsIgnoreCase("youtube")) {
            openYouTubeVideo(selectedTrailer.getKey());
        }
    }

    private void openYouTubeVideo(String videoID) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID)));
    }

    public void favourite(View view) {

        boolean isExist = checkIfFavourite(result);

        if (!isExist) {
            addMovieToContentProvider(result);
        } else {
            deleteFavourite(result);
        }
    }


    public boolean checkIfFavourite(Result result) {
        boolean isExist = false;
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.column_id,
                null,
                null);

        if (cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(0));
            if (id == result.getId()) {
                isExist = true;
            }
        }
        return isExist;
    }

    public void drawFavourite() {

        boolean isExist = checkIfFavourite(result);

        if (isExist) {
            favBtn.setBackgroundColor(Color.parseColor("#555DBCD2"));
        } else {
            favBtn.setBackgroundColor(Color.parseColor("#e6e6e6"));
        }
    }


    private void addMovieToContentProvider(Result movie) {

        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.column_id, movie.getId());
        values.put(MovieContract.MovieEntry.column_voteCount, movie.getVoteCount());
        values.put(MovieContract.MovieEntry.column_voteAverage, movie.getVoteAverage());
        values.put(MovieContract.MovieEntry.column_title, movie.getTitle());
        values.put(MovieContract.MovieEntry.column_popularity, movie.getPopularity());
        values.put(MovieContract.MovieEntry.column_posterPath, movie.getPosterPath());
        values.put(MovieContract.MovieEntry.column_originalLanguage, movie.getOriginalLanguage());
        values.put(MovieContract.MovieEntry.column_originalTitle, movie.getOriginalTitle());
        values.put(MovieContract.MovieEntry.column_overview, movie.getOverview());
        values.put(MovieContract.MovieEntry.column_releaseDate, movie.getReleaseDate());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        if (uri != null) {
            displayToast("Successfully Marked");
            favBtn.setBackgroundColor(Color.parseColor("#555DBCD2"));
        }
    }

    private boolean deleteFavourite(Result movie) {

        int deleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.column_id + "=" + movie.getId(),
                null);

        if (deleted != -1) {
            favBtn.setBackgroundColor(Color.parseColor("#e6e6e6"));
            return true;
        } else {
            return false;
        }
    }
}
