package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.common.Consts.Modules.UCMoviesName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.kodi.annotations.BindSingle

@BindSingle(
        toClass = IGetCachedMoviesUseCase::class,
        toModule = UCMoviesName
)
class GetCachedMoviesUseCase(
    private val repository: IMoviesRepository
) : IGetCachedMoviesUseCase {

    private var lastGenreId: Int = -1
    private var cachedResult: SResult.Success<List<MovieUI>> = successResult(listOf())

    override suspend fun invoke(data: Int): SResult<List<MovieUI>> {
        return checkGenreChanged(data)
                .run {
                    if(cachedResult.data.isNotEmpty()) {
                        return cachedResult
                    }
                    repository.getLocalMovies(lastGenreId)
                }.let { localList ->
                    if (repository.hasResults()) localList
                    else emptyResult()
                }.mapListTo()
                .also {
                    if(it is SResult.Success<List<MovieUI>>) cachedResult = it
                }
    }

    private fun checkGenreChanged(genreId: Int) {
        if(genreId != lastGenreId) {
            lastGenreId = genreId
            cachedResult = successResult(listOf())
            repository.clear()
        }
    }
}

interface IGetCachedMoviesUseCase : IUseCase.InOut<Int, SResult<List<MovieUI>>>