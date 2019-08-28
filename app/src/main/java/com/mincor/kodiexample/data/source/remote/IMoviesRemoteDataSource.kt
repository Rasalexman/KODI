package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.remote.MovieModel

interface IMoviesRemoteDataSource {

    suspend fun getByGenreId(genreId: Int): SResult<List<MovieModel>>

    suspend fun getMovieDetails(movieId: Int): SResult<MovieModel>

    suspend fun getNewMoviesByGenreId(genreId: Int): SResult<List<MovieModel>>

    fun clearPage()
}