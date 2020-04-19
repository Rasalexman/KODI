package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.mincor.kodiexample.data.repository.MoviesRepository

class GetNewMoviesUseCase (
    val repository: MoviesRepository
) {
    suspend fun invoke(genreId: Int?): SResult<List<MovieUI>> = genreId?.let {
        repository.getNewRemoteMovies(genreId).also {
            saveResult(it)
        }.mapListTo()
    } ?: emptyResult()

    private suspend fun saveResult(result: SResult<List<MovieEntity>>) {
        if (result is SResult.Success && result.data.isNotEmpty()) {
            repository.saveMovies(result.data)
        }
    }
}