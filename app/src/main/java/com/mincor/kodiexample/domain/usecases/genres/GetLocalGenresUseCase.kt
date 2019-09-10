package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.presentation.genres.GenreUI
import com.mincor.kodiexample.data.repository.GenresRepository

class GetLocalGenresUseCase(
    private val repository: GenresRepository
) {
    suspend fun execute(): SResult<List<GenreUI>> =
        repository.getLocalGenreList().mapListTo()
}