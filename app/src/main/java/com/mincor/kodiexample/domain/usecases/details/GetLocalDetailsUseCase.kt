package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.common.Consts.Modules.UCDetailsName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.ILocalMoviesRepository
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.WithInstance

@BindProvider(
        toClass = IGetLocalDetailsUseCase::class,
        toModule = UCDetailsName
)
class GetLocalDetailsUseCase(
       @WithInstance(with = IMoviesRepository::class)
       private val moviesRepository: ILocalMoviesRepository
) : IGetLocalDetailsUseCase {
    override suspend fun invoke(data: Int): SResult<MovieEntity> = moviesRepository.getLocalMovieById(data)
}

interface IGetLocalDetailsUseCase : IUseCase.InOut<Int, SResult<MovieEntity>>