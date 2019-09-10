package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetMoviesUseCase(
        private val repository: MoviesRepository,
        private val getCachedMoviesUseCase: GetCachedMoviesUseCase,
        private val getRemoteMoviesUseCase: GetRemoteMoviesUseCase
) : AsyncTasksManager() {

    suspend fun execute(genreId: Int?): SResult<List<MovieUI>> = doAsyncAwait {
        if(genreId != null) {
            repository.clear()
            when (val localResult = getCachedMoviesUseCase.execute(genreId)) {
                is SResult.Empty -> getRemoteMoviesUseCase.execute(genreId)
                is SResult.Success -> localResult
                is SResult.Error -> localResult
            }
        } else {
            emptyResult()
        }
    }
}