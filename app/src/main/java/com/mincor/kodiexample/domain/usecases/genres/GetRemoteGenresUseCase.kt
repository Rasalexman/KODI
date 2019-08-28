package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.ui.genres.GenreUI
import com.mincor.kodiexample.data.repository.GenresRepository

class GetRemoteGenresUseCase(
    private val repository: GenresRepository
) {
    suspend fun execute(): SResult<List<GenreUI>> {
        return repository.getRemoteGenresList().let { result ->
            if (result is SResult.Success && result.data.isNotEmpty()) {
                repository.saveGenres(result.data)
            }
            result.mapListTo()
        }
    }
}