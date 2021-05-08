package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.common.Consts.Modules.RDSName
import com.mincor.kodiexample.common.getResult
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.model.remote.GenreModel
import com.mincor.kodiexample.providers.network.api.IMovieApi
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
        return moviesApi.getGenresList().getResult { it.genres }
    }

    override suspend fun getGenresImages(result: List<GenreEntity>) {
        result.forEach { genreEntity ->
            moviesApi.getMoviesListByPopularity(genreEntity.id ?: 0).run {
                body()?.let { moviesResult ->
                    moviesResult.results.filter {
                        val path = it.poster_path ?: it.backdrop_path.orEmpty()
                        path.isNotEmpty() && !addedImages.contains(path)
                    }.take(3).mapTo(genreEntity.images) {
                        val imagePoster = it.poster_path ?: it.backdrop_path.orEmpty()
                        addedImages.add(imagePoster)
                        imagePoster
                    }
                }
            }
        }

    }
}