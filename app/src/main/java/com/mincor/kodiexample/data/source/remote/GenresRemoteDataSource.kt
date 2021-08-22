package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.common.Consts.Modules.RDSName
import com.mincor.kodiexample.common.getResult
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.errorResult
import com.mincor.kodiexample.data.dto.toSuccessResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.model.remote.GenreModel
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.rasalexman.coroutinesmanager.IAsyncTasksManager
import com.rasalexman.coroutinesmanager.doTryCatchAsyncAwait
import com.rasalexman.kodi.annotations.BindSingle

@BindSingle(
        toClass = IGenresRemoteDataSource::class,
        toModule = RDSName
)
class GenresRemoteDataSource(
        private val moviesApi: IMovieApi
) : IGenresRemoteDataSource {

    private val addedImages by lazy { mutableSetOf<String>() }

    override suspend fun getRemoteGenresList(): SResult<List<GenreModel>> {
        return doTryCatchAsyncAwait(tryBlock = {
            moviesApi.getGenresList().getResult { it.genres }
        }, catchBlock = {
            errorResult(message = it.message.orEmpty(), code = 101)
        })
    }

    override suspend fun getGenresImages(result: List<GenreEntity>): SResult<List<GenreEntity>> {
        return doTryCatchAsyncAwait(tryBlock = {
            result.forEach { genreEntity ->
                val genreId = genreEntity.id ?: 0
                moviesApi.getMoviesListByPopularity(genreId).run {
                    body()?.let { moviesResult ->
                        moviesResult.results.filter {
                            val path = it.poster_path ?: it.backdrop_path.orEmpty()
                            val isPathAdded = path.isNotEmpty() && !addedImages.contains(path)
                            isPathAdded
                        }.take(DEFAULT_POSTER_COUNT).mapTo(genreEntity.images) {
                            val imagePoster = it.poster_path ?: it.backdrop_path.orEmpty()
                            println("-----> imagePoster = $imagePoster")
                            addedImages.add(imagePoster)
                            imagePoster
                        }
                    }
                }
            }
            result.toSuccessResult()
        }, catchBlock = {
            println("-----> getGenresImages Error = $it")
            errorResult(101, it.message.orEmpty())
        })
    }

    companion object {
        private const val DEFAULT_POSTER_COUNT = 3
    }
}

interface IGenresRemoteDataSource : IAsyncTasksManager {
    suspend fun getRemoteGenresList(): SResult<List<GenreModel>>
    suspend fun getGenresImages(result: List<GenreEntity>): SResult<List<GenreEntity>>
}