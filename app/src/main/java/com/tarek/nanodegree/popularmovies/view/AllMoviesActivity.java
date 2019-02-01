package com.tarek.nanodegree.popularmovies.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tarek.nanodegree.popularmovies.R;
import com.tarek.nanodegree.popularmovies.model.backend.API;
import com.tarek.nanodegree.popularmovies.model.backend.MovieAPI;
import com.tarek.nanodegree.popularmovies.model.pojo.DiscoverMoviesResponse;
import com.tarek.nanodegree.popularmovies.model.pojo.Result;
import com.tarek.nanodegree.popularmovies.model.providers.MovieContract;
import com.tarek.nanodegree.popularmovies.view.adapters.MovieAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllMoviesActivity extends AppCompatActivity implements MovieAdapter.OnItemClicked {

    private RecyclerView allMoviesRecyclerView;
    private MovieAdapter mRecyclerViewAdapter;
    private ArrayList<Result> allMovies;
    private ProgressDialog progressDialog;
    private Retrofit retrofit;
    private MovieAPI movieAPI;
    private static final int TOP_RATED = 0;
    private static final int MOST_POPULAR = 1;
    private static final int MY_FAVOURITE = 2;

    private int state = MOST_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI //
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        int columns = 2;
        allMovies = new ArrayList<>(4);//
        mRecyclerViewAdapter = new MovieAdapter(this, allMovies);
        mRecyclerViewAdapter.setOnClick(this); // Bind the listener

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);


        gridLayoutManager.onSaveInstanceState();


        allMoviesRecyclerView = (RecyclerView) findViewById(R.id.all_movies_recycler_view);
        allMoviesRecyclerView.setLayoutManager(gridLayoutManager);
        allMoviesRecyclerView.setAdapter(mRecyclerViewAdapter);

        // back-end call //
        retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        movieAPI = retrofit.create(MovieAPI.class);

        if (savedInstanceState != null) {
            state = savedInstanceState.getInt("state");
        }

        if (state == MOST_POPULAR) {
            //default view
            loadMovies(MOST_POPULAR);
            state = MOST_POPULAR;

        } else if (state == TOP_RATED) {

            loadMovies(TOP_RATED);
            state = TOP_RATED;

        } else if (state == MY_FAVOURITE) {

            allMovies = getAllFavouriteMovies();
            mRecyclerViewAdapter.updateDataSet(allMovies);
            state = MY_FAVOURITE;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public void onItemClick(int position) {

        String details = new Gson().toJson(allMovies.get(position));

        Intent intent = new Intent(AllMoviesActivity.this, MovieDetailsActivity.class);
        intent.putExtra("details", details);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.top:
                loadMovies(TOP_RATED);
                state = TOP_RATED;
                return true;
            case R.id.pop:
                loadMovies(MOST_POPULAR);
                state = MOST_POPULAR;
                return true;
            case R.id.fav:
                allMovies = getAllFavouriteMovies();
                mRecyclerViewAdapter.updateDataSet(allMovies);
                state = MY_FAVOURITE;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMovies(int by) {

        Call<DiscoverMoviesResponse> call = null;

        if (by == MOST_POPULAR) {
            call = movieAPI.getPopularMovie(API.API_KEY_V3);
        } else if (by == TOP_RATED) {
            call = movieAPI.getTopRatedMovie(API.API_KEY_V3);
        }

        call.enqueue(new Callback<DiscoverMoviesResponse>() {
            @Override
            public void onResponse(Call<DiscoverMoviesResponse> call, Response<DiscoverMoviesResponse> response) {

                DiscoverMoviesResponse response1 = response.body();
                allMovies = (ArrayList<Result>) response1.getResults();
                mRecyclerViewAdapter.updateDataSet((ArrayList<Result>) response1.getResults());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<DiscoverMoviesResponse> call, Throwable t) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(AllMoviesActivity.this, getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", t.toString());

            }
        });
    }

    private void displayToast(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }


    public ArrayList<Result> getAllFavouriteMovies() {
        ArrayList<Result> movies = new ArrayList<Result>();

        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Result movie = new Result();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setVoteCount(Integer.parseInt(cursor.getString(1)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(2)));
                movie.setTitle(cursor.getString(3));
                movie.setPopularity(Double.parseDouble(cursor.getString(4)));
                movie.setPosterPath(cursor.getString(5));
                movie.setOriginalLanguage(cursor.getString(6));
                movie.setOriginalTitle(cursor.getString(7));
                movie.setOverview(cursor.getString(8));
                movie.setReleaseDate(cursor.getString(9));

                movies.add(movie);
            } while (cursor.moveToNext());
        }

        return movies;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("state", state);
        super.onSaveInstanceState(outState);
    }

}
