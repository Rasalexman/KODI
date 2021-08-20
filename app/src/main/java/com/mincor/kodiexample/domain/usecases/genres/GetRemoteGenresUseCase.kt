package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.applyIfSuccessSuspend
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.IGenresRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreItem
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
    toClass = IGetRemoteGenresUseCase::class,
    toModule = Consts.Modules.UCGenresName
)
class GetRemoteGenresUseCase(
    private val repository: IGenresRepository
) : IGetRemoteGenresUseCase {
    override suspend fun invoke(): SResult<List<GenreItem>> {
        return repository.getRemoteGenresList().applyIfSuccessSuspend { data ->
            repository.saveGenres(data)
        }.mapListTo()
    }
}

interface IGetRemoteGenresUseCase : IUseCase.Out<SResult<List<GenreItem>>>