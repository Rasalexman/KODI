package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.activity.log
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.errorResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.remote.MovieModel
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.mincor.kodiexample.providers.network.responses.GetMoviesByGenreIdResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult

class MoviesRemoteDataSource(
    private val moviesApi: IMovieApi
) : IMoviesRemoteDataSource {

    private var currentPage = 1
    private var maxPages = Consts.PAGES_DEFAULT_MAX_COUNT
    private val mutex = Mutex()

    private val isCanChangePage: Boolean
        get() = currentPage < maxPages

    override suspend fun getByGenreId(genreId: Int): SResult<List<MovieModel>> =
        if(isCanChangePage) {
            requestHandler(genreId = genreId, page = currentPage, needChangePage = true)
        } else emptyResult()

    private suspend fun requestHandler(genreId: Int, page: Int, needChangePage: Boolean = true) = mutex.withLock {
        moviesApi.getMoviesListByGenreId(genreId, page).run {
            this.body()?.let { resultValue ->
                if(needChangePage) changePage(resultValue)
                successResult(resultValue.results)
            } ?: this.errorBody()?.let {
                errorResult(this.code(), this.message())
            } ?: emptyResult()
        }
    }

    override suspend fun getNewMoviesByGenreId(genreId: Int): SResult<List<MovieModel>> {
        return requestHandler(genreId = genreId, page = 1, needChangePage = false)
    }

    override suspend fun getMovieDetails(movieId: Int): SResult<MovieModel> {
        return when(val result = moviesApi.getMovieDetails(movieId).awaitResult()) {
            is Result.Ok -> {
                log { "HERE IS A RESULT OK ${result.value}" }
                successResult(result.value)
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

    override fun upPage() {
        currentPage = 2
    }

    /**
     * Clear Paging
     */
    override fun clearPage() {
        currentPage = 1
        maxPages = Consts.PAGES_DEFAULT_MAX_COUNT
    }

    /**
     * Increase currentPage count if available
     *
     * @param response - current request response data
     */
    private fun changePage(response: GetMoviesByGenreIdResponse) {
        val currentPage = response.page
        maxPages = response.total_pages
        if(currentPage < maxPages) this.currentPage++
    }
}