package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.applyIfSuccessSuspend
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.data.repository.IRemoteMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.WithInstance

@BindProvider(
        toClass = IGetNewMoviesUseCase::class,
        toModule = Consts.Modules.UCMoviesName
)
class GetNewMoviesUseCase (
    @WithInstance(with = IMoviesRepository::class)
    private val moviesRepository: IRemoteMoviesRepository
) : IGetNewMoviesUseCase {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun invoke(genreId: Int?): SResult<List<MovieUI>> = genreId?.let {
        moviesRepository.getNewRemoteMovies(genreId).applyIfSuccessSuspend {
            moviesRepository.saveMovies(it)
        }.mapListTo()
    } ?: emptyResult()
}

interface IGetNewMoviesUseCase : IUseCase.InOut<Int?, SResult<List<MovieUI>>>