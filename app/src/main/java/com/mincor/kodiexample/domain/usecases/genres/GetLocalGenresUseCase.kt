package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.IGenresRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreItem

class GetLocalGenresUseCase(
    private val repository: IGenresRepository
) : IGetLocalGenresUseCase {
    override suspend fun invoke(): SResult<List<GenreItem>> =
        repository.getLocalGenreList().mapListTo()
}

interface IGetLocalGenresUseCase : IUseCase.Out<SResult<List<GenreItem>>>