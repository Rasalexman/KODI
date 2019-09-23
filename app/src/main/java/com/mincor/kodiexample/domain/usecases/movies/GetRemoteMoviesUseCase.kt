package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI

class GetRemoteMoviesUseCase(
    private val repository: MoviesRepository
) : IUseCase.InOut<Int, SResult<List<MovieUI>>> {
    override suspend fun execute(data: Int): SResult<List<MovieUI>> {
        val result = repository.getRemoteMovies(data)
        if(!repository.hasLocalResults) saveResult(result)
        return result.mapListTo()
    }

    private suspend fun saveResult(result: SResult<List<MovieEntity>>) {
        if (result is SResult.Success && result.data.isNotEmpty()) {
            repository.saveMovies(result.data)
        }
    }
}