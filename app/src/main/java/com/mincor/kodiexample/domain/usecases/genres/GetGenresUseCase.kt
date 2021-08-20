package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.flatMapIfEmptySuspend
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreItem
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.WithInstance

@BindProvider(
    toClass = IGenresOutUseCase::class,
    toModule = Consts.Modules.UCGenresName,
    /*atScope = Consts.Scopes.GENRES,*/
    toTag = Consts.Tags.GENRE_USE_CASE
)
class GetGenresUseCase(
    @WithInstance(
        scope = Consts.Scopes.GENRES,
        tag = Consts.Tags.GENRE_LOCAL_USE_CASE,
        with = IGetLocalGenresUseCase::class
    )
    private val getLocalGenresUseCase: IGetLocalGenresUseCase,
    private val getRemoteGenresUseCase: IGetRemoteGenresUseCase
) : AsyncTasksManager(), IGenresOutUseCase {
    override suspend fun invoke(): SResult<List<GenreItem>> = doAsyncAwait {
        getLocalGenresUseCase.invoke().flatMapIfEmptySuspend {
            getRemoteGenresUseCase.invoke()
        }
    }
}

interface IGenresOutUseCase : IUseCase.Out<SResult<List<GenreItem>>>