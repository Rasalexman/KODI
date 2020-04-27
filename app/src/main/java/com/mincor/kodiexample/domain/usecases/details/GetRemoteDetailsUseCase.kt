package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
        toClass = IGetRemoteDetailsUseCase::class,
        toModule = Consts.Modules.UCDetailsName
)
class GetRemoteDetailsUseCase(
        private val moviesRepository: IMoviesRepository
) : IGetRemoteDetailsUseCase {
    override suspend fun invoke(data: Int): SResult<MovieEntity> {
        return moviesRepository
                .getRemoteMovieById(data)
                .let { remoteResult ->
                    if (remoteResult is SResult.Success) {
                        moviesRepository.saveMovie(remoteResult.data.apply {
                            hasDetails = true
                        })
                    }
                    remoteResult
                }
    }
}

interface IGetRemoteDetailsUseCase : IUseCase.InOut<Int, SResult<MovieEntity>>