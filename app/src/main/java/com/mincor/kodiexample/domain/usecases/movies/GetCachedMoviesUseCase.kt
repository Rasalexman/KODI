package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.presentation.movies.MovieUI

class GetCachedMoviesUseCase(
    private val repository: MoviesRepository
) {
    suspend fun execute(genreId: Int): SResult<List<MovieUI>> {
        return repository.getLocalMovies(genreId).let { localList ->
            if (repository.hasLocalResults) localList
            else emptyResult()
        }.mapListTo()
    }
}