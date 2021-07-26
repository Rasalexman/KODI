package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.applyIfSuccessSuspend
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.data.repository.IRemoteMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.WithInstance

@BindProvider(
        toClass = IGetRemoteMoviesUseCase::class,
        toModule = Consts.Modules.UCMoviesName
)
class GetRemoteMoviesUseCase(
    @WithInstance(with = IMoviesRepository::class)
    private val moviesRepository: IRemoteMoviesRepository
) : IGetRemoteMoviesUseCase {
    override suspend fun invoke(data: Int): SResult<List<MovieUI>> {
        val result = moviesRepository.getRemoteMovies(data).applyIfSuccessSuspend {
            if(!moviesRepository.hasResults()) {
                moviesRepository.saveMovies(it)
            }
        }
        return result.mapListTo()
    }
}

interface IGetRemoteMoviesUseCase : IUseCase.InOut<Int, SResult<List<MovieUI>>>