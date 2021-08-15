package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.common.Consts.Modules.RDSName
import com.mincor.kodiexample.common.getResult
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.errorResult
import com.mincor.kodiexample.data.model.remote.MovieModel
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.mincor.kodiexample.providers.network.responses.GetMoviesByGenreIdResponse
import com.rasalexman.coroutinesmanager.IAsyncTasksManager
import com.rasalexman.coroutinesmanager.doTryCatchAsyncAwait
import com.rasalexman.kodi.annotations.BindSingle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@BindSingle(
    toClass = IMoviesRemoteDataSource::class,
    toModule = RDSName
)
class MoviesRemoteDataSource(
    private val moviesApi: IMovieApi
) : IMoviesRemoteDataSource {

    private var currentPage = 1
    private var maxPages = Consts.PAGES_DEFAULT_MAX_COUNT
    private val mutex = Mutex()

    private val isCanChangePage: Boolean
        get() = currentPage < maxPages


    /**
     * Get movies by genre id with pagination
     */
    override suspend fun getByGenreId(genreId: Int): SResult<List<MovieModel>> =
        doTryCatchAsyncAwait(tryBlock = {
            if (isCanChangePage) {
                requestHandler(genreId = genreId, page = currentPage, needChangePage = true)
            } else emptyResult()
        }, catchBlock = {
            errorResult(code = 102, message = it.message.orEmpty())
        })


    /**
     * Get New movies list by genre id starting from first page (for swipe to refresh)
     */
    override suspend fun getNewMoviesByGenreId(genreId: Int): SResult<List<MovieModel>> {
        return doTryCatchAsyncAwait(tryBlock = {
            requestHandler(genreId = genreId, page = 1, needChangePage = false)
        }, catchBlock = {
            errorResult(code = 103, message = it.message.orEmpty())
        })
    }

    /**
     * Get all the movie details for current id
     */
    override suspend fun getMovieDetails(movieId: Int): SResult<MovieModel> {
        return doTryCatchAsyncAwait(tryBlock = {
            moviesApi.getMovieDetails(movieId).getResult { it }
        }, catchBlock = {
            errorResult(code = 104, message = it.message.orEmpty())
        })
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
     * Request data from API
     */
    private suspend fun requestHandler(genreId: Int, page: Int, needChangePage: Boolean = true) =
        mutex.withLock {
            moviesApi.getMoviesListByGenreId(genreId, page).getResult { response ->
                if (needChangePage) changePage(response)
                response.results
            }
        }

    /**
     * Increase currentPage count if available
     *
     * @param response - current request response data
     */
    private fun changePage(response: GetMoviesByGenreIdResponse) {
        val currentPage = response.page
        maxPages = response.total_pages
        if (currentPage < maxPages) this.currentPage++
    }
}

interface IMoviesRemoteDataSource : IAsyncTasksManager {
    suspend fun getByGenreId(genreId: Int): SResult<List<MovieModel>>
    suspend fun getMovieDetails(movieId: Int): SResult<MovieModel>
    suspend fun getNewMoviesByGenreId(genreId: Int): SResult<List<MovieModel>>
    fun clearPage()
    fun upPage()
}