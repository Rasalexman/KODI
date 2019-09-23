package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.instance
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetNextMoviesUseCase : AsyncTasksManager(), IKodi, IUseCase.InOut<Int, SResult<List<MovieUI>>> {

    override suspend fun execute(data: Int): SResult<List<MovieUI>> = doAsyncAwait {
        instance<GetRemoteMoviesUseCase>().execute(data)
    }
}