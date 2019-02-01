package com.tarek.nanodegree.popularmovies.model.backend;

import com.tarek.nanodegree.popularmovies.model.pojo.DiscoverMoviesResponse;
import com.tarek.nanodegree.popularmovies.model.pojo.MovieReviews;
import com.tarek.nanodegree.popularmovies.model.pojo.MovieTrailers;

 import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tarek.abdulkader on 10/31/2017.
 */

public interface MovieAPI {

    @GET("movie/top_rated")
    Call<DiscoverMoviesResponse> getTopRatedMovie(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<DiscoverMoviesResponse> getPopularMovie(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call <MovieTrailers> getVideos(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<MovieReviews> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

}
