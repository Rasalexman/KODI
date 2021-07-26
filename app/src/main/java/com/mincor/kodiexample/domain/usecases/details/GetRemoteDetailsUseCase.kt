package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.applyIfSuccessSuspend
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.data.repository.IRemoteMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.WithInstance

@BindProvider(
    toClass = IGetRemoteDetailsUseCase::class,
    toModule = Consts.Modules.UCDetailsName
)
class GetRemoteDetailsUseCase(
    @WithInstance(with = IMoviesRepository::class)
    private val moviesRepository: IRemoteMoviesRepository
) : IGetRemoteDetailsUseCase {
    override suspend fun invoke(data: Int): SResult<MovieEntity> {
        return moviesRepository.getRemoteMovieById(data).applyIfSuccessSuspend {
            it.hasDetails = true
            moviesRepository.saveMovie(it)
        }
    }
}

interface IGetRemoteDetailsUseCase : IUseCase.InOut<Int, SResult<MovieEntity>>