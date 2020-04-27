package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
        toClass = IGetNewMoviesUseCase::class,
        toModule = Consts.Modules.UCMoviesName
)
class GetNewMoviesUseCase (
    private val repository: IMoviesRepository
) : IGetNewMoviesUseCase {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun invoke(genreId: Int?): SResult<List<MovieUI>> = genreId?.let {
        repository.getNewRemoteMovies(genreId).also {
            saveResult(it)
        }.mapListTo()
    } ?: emptyResult()

    private suspend fun saveResult(result: SResult<List<MovieEntity>>) {
        if (result is SResult.Success && result.data.isNotEmpty()) {
            repository.saveMovies(result.data)
        }
    }
}

interface IGetNewMoviesUseCase : IUseCase.InOut<Int?, SResult<List<MovieUI>>>