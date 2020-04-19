package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.domain.usecases.details.GetLocalDetailsUseCase
import com.mincor.kodiexample.domain.usecases.details.GetRemoteDetailsUseCase
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetMovieDetailUseCase(
        private val getLocalDetailsUseCase: GetLocalDetailsUseCase,
        private val getRemoteDetailsUseCase: GetRemoteDetailsUseCase
) : IUseCase.InOut<Int, SResult<MovieEntity>>, AsyncTasksManager() {

    override suspend fun invoke(data: Int): SResult<MovieEntity> = doAsyncAwait {
        getLocalDetailsUseCase.invoke(data).let {
            if (it is SResult.Success) it
            else getRemoteDetailsUseCase.invoke(data)
        }
    }
}