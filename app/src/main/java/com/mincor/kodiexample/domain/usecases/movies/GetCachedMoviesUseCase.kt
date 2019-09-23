package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI

class GetCachedMoviesUseCase(
    private val repository: MoviesRepository
) : IUseCase.InOut<Int, SResult<List<MovieUI>>> {

    private var lastGenreId: Int = -1

    override suspend fun execute(data: Int): SResult<List<MovieUI>> {
        return checkGenreChanged(data)
                .run {
                    repository.getLocalMovies(lastGenreId)
                }.let { localList ->
                    if (repository.hasLocalResults) localList
                    else emptyResult()
                }.mapListTo()
    }

    private fun checkGenreChanged(genreId: Int) {
        if(genreId != lastGenreId) {
            lastGenreId = genreId
            repository.clear()
        }
    }
}