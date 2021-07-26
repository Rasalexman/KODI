package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
    toClass = IGetNextMoviesUseCase::class,
    toModule = Consts.Modules.UCMoviesName
)
class GetNextMoviesUseCase(
    private val getRemoteMoviesUseCase: IGetRemoteMoviesUseCase
) : AsyncTasksManager(), IGetNextMoviesUseCase {
    override suspend fun invoke(data: Int): SResult<List<MovieUI>> = doAsyncAwait {
        getRemoteMoviesUseCase.invoke(data)
    }
}

interface IGetNextMoviesUseCase : IUseCase.InOut<Int, SResult<List<MovieUI>>>