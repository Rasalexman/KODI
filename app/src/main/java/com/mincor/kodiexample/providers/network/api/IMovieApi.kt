package com.mincor.kodiexample.providers.network.api

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.model.remote.MovieModel
import com.mincor.kodiexample.providers.network.responses.GetGenresResponse
import com.mincor.kodiexample.providers.network.responses.GetMoviesByGenreIdResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieApi {

    /**
     * Get the list of official genres for movies.
     */
    @GET("genre/movie/list")
    suspend fun getGenresList(): Response<GetGenresResponse>

    @GET("discover/movie")
    suspend fun getMoviesListByGenreId(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("release_date.lte") lte: String = Consts.currentDate
    ): Response<GetMoviesByGenreIdResponse>

    @GET("discover/movie")
    suspend fun getMoviesListByPopularity(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("sort_by") sort_by: String = "popularity.desc"
    ): Response<GetMoviesByGenreIdResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): Call<MovieModel>
}