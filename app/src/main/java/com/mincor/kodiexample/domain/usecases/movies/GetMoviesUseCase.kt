package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.ui.movies.MovieUI
import com.mincor.kodiexample.data.repository.MoviesRepository

class GetMoviesUseCase(
        private val repository: MoviesRepository,
        private val getCachedMoviesUseCase: GetCachedMoviesUseCase,
        private val getRemoteMoviesUseCase: GetRemoteMoviesUseCase
) {

    suspend fun execute(genreId: Int): SResult<List<MovieUI>> {
        return let {
            repository.clear()
            getCachedMoviesUseCase.execute(genreId)
            //getRemoteMoviesUseCase.execute(genreId)
        }
    }
}