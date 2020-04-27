package com.mincor.kodiexample.presentation.details

import com.mincor.kodiexample.R
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.movies.IGetMovieDetailUseCase
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.launchOnUITryCatch
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.instance
import com.rasalexman.sticky.core.IStickyPresenter

class DetailsPresenter : IStickyPresenter<IDetailsView>, ICoroutinesManager, IKodi {

    var movieId: Int = 0

    override fun onViewCreated(view: IDetailsView) = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = instance<IGetMovieDetailUseCase>().invoke(movieId)
                view().sticky {
                    when (result) {
                        is SResult.Success -> showDetails(result.data)
                        is SResult.Error -> showToast(result.message)
                    }
                }
            },
            catchBlock = {
                view().showToast(it.message ?: R.string.error_loading_details)
            }
    )

    override fun onViewDestroyed() {
        super.onViewDestroyed()
        cleanup()
    }
}