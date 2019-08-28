package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodi.core.applyIf
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.model.ui.movies.MovieUI
import com.mincor.kodiexample.data.repository.MoviesRepository

class GetRemoteMoviesUseCase(
    private val repository: MoviesRepository
) {
    suspend fun execute(genreId: Int?): SResult<List<MovieUI>> = genreId?.let {
        repository.getRemoteMovies(genreId).applyIf(!repository.hasLocalResults) {
            //saveResult(it)
        }.mapListTo()
    } ?: emptyResult()

    private suspend fun saveResult(result: SResult<List<MovieEntity>>) {
        if (result is SResult.Success && result.data.isNotEmpty()) {
            repository.saveMovies(result.data)
        }
    }
}