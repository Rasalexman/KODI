package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.instance

class GetNextMoviesUseCase : AsyncTasksManager(), IKodi, IGetNextMoviesUseCase {

    override suspend fun invoke(data: Int): SResult<List<MovieUI>> = doAsyncAwait {
        instance<IGetRemoteMoviesUseCase>().invoke(data)
    }
}

interface IGetNextMoviesUseCase : IUseCase.InOut<Int, SResult<List<MovieUI>>>