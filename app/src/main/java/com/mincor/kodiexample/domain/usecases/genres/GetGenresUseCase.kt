package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreItem
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetGenresUseCase(
        private val getLocalGenresUseCase: GetLocalGenresUseCase,
        private val getRemoteGenresUseCase: GetRemoteGenresUseCase
) : AsyncTasksManager(), IGenresOutUseCase {
    override suspend fun invoke(): SResult<List<GenreItem>> = doAsyncAwait {
        getLocalGenresUseCase.invoke().let { localResultList ->
            if (localResultList is SResult.Success && localResultList.data.isNotEmpty()) localResultList
            else getRemoteGenresUseCase.invoke()
        }
    }
}

typealias IGenresOutUseCase = IUseCase.Out<SResult<List<GenreItem>>>