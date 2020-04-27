package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.common.Consts.Modules.UCGenresName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.IGenresRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreItem
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
        toClass = IGetLocalGenresUseCase::class,
        toModule = UCGenresName
)
class GetLocalGenresUseCase(
    private val repository: IGenresRepository
) : IGetLocalGenresUseCase {
    override suspend fun invoke(): SResult<List<GenreItem>> =
        repository.getLocalGenreList().mapListTo()
}

interface IGetLocalGenresUseCase : IUseCase.Out<SResult<List<GenreItem>>>