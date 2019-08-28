package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.providers.database.dao.IMoviesDao

class MoviesLocalDataSource(
    private val moviesDao: IMoviesDao
) : IMoviesLocalDataSource {

    override suspend fun getAll(genreId: Int): SResult.Success<List<MovieEntity>> =
        successResult(moviesDao.getAll(genreId))

    override suspend fun getById(movieId: Int): SResult<MovieEntity> =
        moviesDao.getById(movieId)?.let { localMovie ->
            if(localMovie.hasDetails) successResult(localMovie)
            else emptyResult()
        } ?: emptyResult()

    override suspend fun insertAll(data: List<MovieEntity>) {
        moviesDao.insertAll(data)
    }

    override suspend fun insert(data: MovieEntity) {
        moviesDao.insert(data)
    }
}