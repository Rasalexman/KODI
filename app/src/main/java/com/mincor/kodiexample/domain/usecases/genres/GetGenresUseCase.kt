package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.presentation.genres.GenreUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetGenresUseCase(
        private val getLocalGenresUseCase: GetLocalGenresUseCase,
        private val getRemoteGenresUseCase: GetRemoteGenresUseCase
) : AsyncTasksManager() {
    suspend fun execute(): SResult<List<GenreUI>> = doAsyncAwait {
        getLocalGenresUseCase.execute().let { localResultList ->
            if(localResultList is SResult.Success && localResultList.data.isNotEmpty()) localResultList
            else getRemoteGenresUseCase.execute()
        }
    }
}