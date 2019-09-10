package com.mincor.kodiexample.presentation.movies

import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.immutableInstance
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.movies.GetMoviesUseCase
import com.mincor.kodiexample.domain.usecases.movies.GetNextMoviesUseCase
import com.mincor.kodiexample.mvp.base.lifecycle.BasePresenter
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.SuspendCatch
import com.rasalexman.coroutinesmanager.launchOnUITryCatch

class MoviesPresenter(
        coroutinesManager: ICoroutinesManager
) : BasePresenter<MoviesContract.IView>(),
        MoviesContract.IPresenter, IKodi, ICoroutinesManager by coroutinesManager {

    private val getMoviesUseCase: GetMoviesUseCase by immutableInstance()
    private val getNextMoviesUseCase: GetNextMoviesUseCase by immutableInstance()

    private val catchBlock: SuspendCatch<Unit> = {
        view().showError(it.message.orEmpty())
        view().hideLoading()
    }

    override fun getMoviesByGenreId(genreId: Int?) = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                when(val result = getMoviesUseCase.execute(genreId)) {
                    is SResult.Success -> view().showItems(result.data)
                    is SResult.Error -> view().showError(result.message)
                }
            },
            catchBlock = catchBlock
    )

    override fun getNextMoviesByGenreId(genreId: Int?) = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                when(val result = getNextMoviesUseCase.execute(genreId)) {
                    is SResult.Success -> view().addItems(result.data)
                    is SResult.Error -> view().showError(result.message)
                }
            }, catchBlock = catchBlock
    )
}