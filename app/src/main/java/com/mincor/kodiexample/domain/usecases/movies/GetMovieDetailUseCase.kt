package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.details.GetLocalDetailsUseCase
import com.mincor.kodiexample.domain.usecases.details.GetRemoteDetailsUseCase

class GetMovieDetailUseCase(
        private val getLocalDetailsUseCase: GetLocalDetailsUseCase,
        private val getRemoteDetailsUseCase: GetRemoteDetailsUseCase
) {

    suspend fun execute(movieId: Int) =
            getLocalDetailsUseCase.execute(movieId).let {
                if (it is SResult.Success) it
                else getRemoteDetailsUseCase.execute(movieId)
            }
}