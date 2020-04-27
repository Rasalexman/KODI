package com.mincor.kodiexample.data.repository

import com.mincor.kodiexample.common.Consts.Modules.RepName
import com.mincor.kodiexample.common.applyIf
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.dto.mapTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.source.local.IMoviesLocalDataSource
import com.mincor.kodiexample.data.source.remote.IMoviesRemoteDataSource
import com.rasalexman.kodi.annotations.BindSingle


@BindSingle(
        toClass = IMoviesRepository::class,
        toModule = RepName
)
class MoviesRepository(
        private val localDataSource: IMoviesLocalDataSource,
        private val remoteDataSource: IMoviesRemoteDataSource
) : IMoviesRepository {

    private var hasLocalResults = false

    override fun hasResults() = hasLocalResults

    override suspend fun getLocalMovies(genreId: Int): SResult<List<MovieEntity>> =
            localDataSource
                    .getAll(genreId)
                    .run {
                        hasLocalResults = data.size >= 19
                        this
                    }.applyIf(hasLocalResults) {
                        remoteDataSource.upPage()
                    }

    override suspend fun getRemoteMovies(genreId: Int): SResult<List<MovieEntity>> {
        return remoteDataSource
                .getByGenreId(genreId)
                .mapListTo()
    }

    override suspend fun getNewRemoteMovies(genreId: Int): SResult<List<MovieEntity>> {
        return remoteDataSource
                .getNewMoviesByGenreId(genreId)
                .mapListTo()
    }

    override suspend fun saveMovies(data: List<MovieEntity>) {
        hasLocalResults = data.isNotEmpty()
        localDataSource.insertAll(data)
    }

    override suspend fun saveMovie(data: MovieEntity) = localDataSource.insert(data)

    override suspend fun getLocalMovieById(movieId: Int): SResult<MovieEntity> =
            localDataSource.getById(movieId)

    override suspend fun getRemoteMovieById(movieId: Int): SResult<MovieEntity> =
            remoteDataSource
                    .getMovieDetails(movieId)
                    .mapTo()

    override fun clear() {
        hasLocalResults = false
        remoteDataSource.clearPage()
    }
}

interface IMoviesRepository {
    fun clear()
    suspend fun saveMovie(data: MovieEntity)
    suspend fun getLocalMovieById(movieId: Int): SResult<MovieEntity>
    suspend fun getRemoteMovieById(movieId: Int): SResult<MovieEntity>
    suspend fun saveMovies(data: List<MovieEntity>)
    suspend fun getNewRemoteMovies(genreId: Int): SResult<List<MovieEntity>>
    suspend fun getRemoteMovies(genreId: Int): SResult<List<MovieEntity>>
    suspend fun getLocalMovies(genreId: Int): SResult<List<MovieEntity>>
    fun hasResults(): Boolean
}