package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.common.Consts.Modules.UCDetailsName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
        toClass = IGetLocalDetailsUseCase::class,
        toModule = UCDetailsName
)
class GetLocalDetailsUseCase(
        private val moviesRepository: IMoviesRepository
) : IGetLocalDetailsUseCase {
    override suspend fun invoke(data: Int): SResult<MovieEntity> = moviesRepository.getLocalMovieById(data)
}

interface IGetLocalDetailsUseCase : IUseCase.InOut<Int, SResult<MovieEntity>>