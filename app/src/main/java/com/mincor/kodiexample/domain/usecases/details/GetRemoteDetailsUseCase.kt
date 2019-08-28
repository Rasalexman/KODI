package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.MoviesRepository

class GetRemoteDetailsUseCase(
    private val moviesRepository: MoviesRepository
) {
    suspend fun execute(movieId: Int): SResult<MovieEntity> {
        return moviesRepository.getRemoteMovieById(movieId).let { remoteResult ->
            if (remoteResult is SResult.Success) {
                moviesRepository.saveMovie(remoteResult.data.apply {
                    hasDetails = true
                })
            }
            remoteResult
        }
    }
}