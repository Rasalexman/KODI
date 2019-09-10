package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.presentation.movies.MovieUI

class GetRemoteMoviesUseCase(
    private val repository: MoviesRepository
) {
    suspend fun execute(genreId: Int?): SResult<List<MovieUI>> = genreId?.let {
        val result = repository.getRemoteMovies(genreId)
        if(!repository.hasLocalResults) saveResult(result)
        result.mapListTo()
    } ?: emptyResult()

    private suspend fun saveResult(result: SResult<List<MovieEntity>>) {
        if (result is SResult.Success && result.data.isNotEmpty()) {
            repository.saveMovies(result.data)
        }
    }
}