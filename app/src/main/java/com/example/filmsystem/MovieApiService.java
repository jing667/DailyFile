package com.example.filmsystem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("/")
    Call<MovieResponse> getMovies(
            @Query("apikey") String apiKey,
            @Query("s") String searchQuery
    );





}

