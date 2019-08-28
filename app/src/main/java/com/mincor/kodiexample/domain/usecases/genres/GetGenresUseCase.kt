package com.mincor.kodiexample.domain.usecases.genres

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.ui.genres.GenreUI

class GetGenresUseCase(
        private val getLocalGenresUseCase: GetLocalGenresUseCase,
        private val getRemoteGenresUseCase: GetRemoteGenresUseCase
) {
    suspend fun execute(): SResult<List<GenreUI>> {
        return getLocalGenresUseCase.execute().let { localResultList ->
            if(localResultList is SResult.Success && localResultList.data.isNotEmpty()) localResultList
            else getRemoteGenresUseCase.execute()
        }
    }
}