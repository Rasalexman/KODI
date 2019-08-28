package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.activity.log
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.errorResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.remote.GenreModel
import com.mincor.kodiexample.providers.network.api.IMovieApi
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult

class GenresRemoteDataSource(
    private val moviesApi: IMovieApi
) : IGenresRemoteDataSource {

    override suspend fun getRemoteGenresList(): SResult<List<GenreModel>> =
        when (val result = moviesApi.getGenresList().awaitResult()) {
            is Result.Ok -> {
                log { "HERE IS A RESULT OK ${Thread.currentThread()}" }
                successResult(result.value.genres)
            }
            is Result.Error -> {
                log { "THERE IS AN ERROR" }
                errorResult(result.response.code(), result.exception.message())
            }
            is Result.Exception -> {
                log { "THERE IS AN EXCEPTION" }
                emptyResult()
            }
        }
}