package com.mincor.kodiexample.presentation.movies

import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.immutableInstance
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.movies.GetMoviesUseCase
import com.mincor.kodiexample.domain.usecases.movies.GetNextMoviesUseCase
import com.mincor.kodiexample.domain.usecases.movies.IGetMoviesInOutUseCase
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.SuspendCatch
import com.rasalexman.coroutinesmanager.launchOnUITryCatch

class MoviesPresenter(
        coroutinesManager: ICoroutinesManager
) : MoviesContract.IPresenter, IKodi, ICoroutinesManager by coroutinesManager {

    override val mustRestoreSticky: Boolean get() = true
    private val getMoviesUseCase: IGetMoviesInOutUseCase by immutableInstance<GetMoviesUseCase>()
    private val getNextMoviesUseCase: IGetMoviesInOutUseCase by immutableInstance<GetNextMoviesUseCase>()

    private val catchBlock: SuspendCatch<Unit> = {
        view().showError(it.message.orEmpty())
        view().hideLoading()
    }

    override var genreId: Int = 0
        set(value) {
            field = value
            onGenreIdChanged()
        }

    private fun onGenreIdChanged() = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = getMoviesUseCase.execute(genreId)
                view().sticky {
                    when (result) {
                        is SResult.Success -> showItems(result.data)
                        is SResult.Error -> showError(result.message)
                    }
                }
            },
            catchBlock = catchBlock
    )

    override fun getNextMoviesByGenreId() = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = getNextMoviesUseCase.execute(genreId)
                view().singleSticky {
                    when (result) {
                        is SResult.Success -> addItems(result.data)
                        is SResult.Error -> showError(result.message)
                    }
                }
            }, catchBlock = catchBlock
    )

    override fun onViewDestroyed() {
        genreId = 0
        cleanup()
    }
}