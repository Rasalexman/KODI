package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.GenresRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreUI

class GetLocalGenresUseCase(
    private val repository: GenresRepository
) : IUseCase.Out<SResult<List<GenreUI>>> {
    override suspend fun execute(): SResult<List<GenreUI>> =
        repository.getLocalGenreList().mapListTo()
}