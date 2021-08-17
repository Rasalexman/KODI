package com.mincor.kodiexample.presentation.movies

import com.mincor.kodiexample.common.Consts.Modules.PresentersName
import com.mincor.kodiexample.common.applyIf
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.movies.IGetMoviesInOutUseCase
import com.mincor.kodiexample.domain.usecases.movies.IGetNextMoviesUseCase
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.SuspendCatch
import com.rasalexman.coroutinesmanager.launchOnUITryCatch
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.kodi.annotations.BindSingle

@BindSingle(
        toClass = MoviesContract.IPresenter::class,
        toModule = PresentersName
)
class MoviesPresenter constructor(
        coroutinesManager: ICoroutinesManager
) : MoviesContract.IPresenter, IKodi, ICoroutinesManager by coroutinesManager {

    override val mustRestoreSticky: Boolean get() = true
    private val getMoviesUseCase: IGetMoviesInOutUseCase by immutableInstance()
    private val getNextMoviesUseCase: IGetNextMoviesUseCase by immutableInstance()

    private val allResults = mutableListOf<MovieUI>()

    private val catchBlock: SuspendCatch<Unit> = {
        view().showError(it.message.orEmpty())
        view().hideLoading()
    }

    override var genreId: Int = 0
        set(value) {
            field = value
            allResults.clear()
            onGenreIdChanged()
        }

    private fun onGenreIdChanged() = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = getMoviesUseCase.invoke(genreId)
                (result as? SResult.Success)?.let {
                    allResults.addAll(result.data)
                }
                view().sticky {
                    when (result) {
                        is SResult.Success -> showItems(allResults)
                        is SResult.Error -> showError(result.message)
                    }
                }
            },
            catchBlock = catchBlock
    )

    override fun getNextMoviesByGenreId() = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = getNextMoviesUseCase.invoke(genreId)
                (result as? SResult.Success)?.let {
                    allResults.addAll(result.data)
                }
                view().singleSticky {
                    when (result) {
                        is SResult.Success -> addItems(result.data)
                        is SResult.Error -> showError(result.message)
                    }
                }
            }, catchBlock = catchBlock
    )

    override fun onViewDestroyed() {
        cleanup()
    }
}