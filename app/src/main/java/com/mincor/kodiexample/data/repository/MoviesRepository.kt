package com.mincor.kodiexample.data.repository

import com.mincor.kodi.core.applyIf
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.dto.mapTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.source.local.IMoviesLocalDataSource
import com.mincor.kodiexample.data.source.remote.IMoviesRemoteDataSource

class MoviesRepository(
        private val localDataSource: IMoviesLocalDataSource,
        private val remoteDataSource: IMoviesRemoteDataSource
) {

    var hasLocalResults = false
        private set

    suspend fun getLocalMovies(genreId: Int): SResult<List<MovieEntity>> =
            localDataSource
                    .getAll(genreId)
                    .run {
                        hasLocalResults = data.size >= 19
                        this
                    }.applyIf(hasLocalResults) {
                        remoteDataSource.upPage()
                    }

    suspend fun getRemoteMovies(genreId: Int): SResult<List<MovieEntity>> {
        return remoteDataSource
                .getByGenreId(genreId)
                .mapListTo()
    }

    suspend fun getNewRemoteMovies(genreId: Int): SResult<List<MovieEntity>> {
        return remoteDataSource
                .getNewMoviesByGenreId(genreId)
                .mapListTo()
    }

    suspend fun saveMovies(data: List<MovieEntity>) {
        hasLocalResults = data.isNotEmpty()
        localDataSource.insertAll(data)
    }

    suspend fun saveMovie(data: MovieEntity) = localDataSource.insert(data)

    suspend fun getLocalMovieById(movieId: Int): SResult<MovieEntity> =
            localDataSource.getById(movieId)

    suspend fun getRemoteMovieById(movieId: Int): SResult<MovieEntity> =
            remoteDataSource
                    .getMovieDetails(movieId)
                    .mapTo()

    fun clear() {
        hasLocalResults = false
        remoteDataSource.clearPage()
    }
}