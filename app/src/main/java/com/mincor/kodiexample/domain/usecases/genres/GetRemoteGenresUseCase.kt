package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.GenresRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.genres.GenreItem

class GetRemoteGenresUseCase(
        private val repository: GenresRepository
) : IUseCase.Out<SResult<List<GenreItem>>> {
    override suspend fun execute(): SResult<List<GenreItem>> {
        return repository
                .getRemoteGenresList()
                .run {
                    if (this is SResult.Success && data.isNotEmpty()) {
                        repository.saveGenres(data)
                    }
                    mapListTo()
                }
    }
}