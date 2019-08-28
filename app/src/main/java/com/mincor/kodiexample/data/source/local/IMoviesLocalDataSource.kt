package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity

interface IMoviesLocalDataSource {
    suspend fun getAll(genreId: Int): SResult.Success<List<MovieEntity>>

    suspend fun getById(movieId: Int): SResult<MovieEntity>

    suspend fun insertAll(data: List<MovieEntity>)

    suspend fun insert(data: MovieEntity)
}