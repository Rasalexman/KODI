package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.common.getResult
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.errorResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.model.remote.GenreModel
import com.mincor.kodiexample.providers.network.api.IMovieApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GenresRemoteDataSource(
        private val moviesApi: IMovieApi
) : IGenresRemoteDataSource {

    private val mutex by lazy { Mutex() }
    private val addedImages by lazy { mutableSetOf<String>() }

    override suspend fun getRemoteGenresList(): SResult<List<GenreModel>> {
        return moviesApi.getGenresList().getResult { it.genres }
    }

    override suspend fun getGenresImages(result: GenreEntity): GenreEntity {
        return mutex.withLock {
            moviesApi.getMoviesListByPopularity(result.id).run {
                body()?.let { moviesResult ->
                    moviesResult.results.filter {
                        val path = it.poster_path.orEmpty()
                        path.isNotEmpty() && !addedImages.contains(path)
                    }.take(3).mapTo(result.images) {
                        val imagePoster = it.poster_path ?: it.backdrop_path.orEmpty()
                        addedImages.add(imagePoster)
                        imagePoster
                    }
                    result
                } ?: result
            }
        }

    }
}