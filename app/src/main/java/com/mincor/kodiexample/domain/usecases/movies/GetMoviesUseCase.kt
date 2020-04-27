package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
        toClass = IGetMoviesInOutUseCase::class,
        toModule = Consts.Modules.UCMoviesName
)
class GetMoviesUseCase(
        private val getCachedMoviesUseCase: IGetCachedMoviesUseCase,
        private val getRemoteMoviesUseCase: IGetRemoteMoviesUseCase
) : AsyncTasksManager(), IGetMoviesInOutUseCase {

    override suspend fun invoke(data: Int): SResult<List<MovieUI>> = doAsyncAwait {
        when (val localResult = getCachedMoviesUseCase.invoke(data)) {
            is SResult.Empty -> getRemoteMoviesUseCase.invoke(data)
            is SResult.Success -> localResult
            is SResult.Error -> localResult
        }
    }
}

interface IGetMoviesInOutUseCase : IUseCase.InOut<Int, SResult<List<MovieUI>>>