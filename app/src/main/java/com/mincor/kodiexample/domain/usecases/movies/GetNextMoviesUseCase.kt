package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.immutableInstance
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetNextMoviesUseCase : AsyncTasksManager(), IKodi {

    private val getRemoteMoviesUseCase: GetRemoteMoviesUseCase by immutableInstance()
    suspend fun execute(genreId: Int?): SResult<List<MovieUI>> = doAsyncAwait {
        getRemoteMoviesUseCase.execute(genreId)
    }
}