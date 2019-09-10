package com.mincor.kodiexample.presentation.genres

import com.mincor.kodi.core.IKodi
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.genres.GetGenresUseCase
import com.mincor.kodiexample.mvp.base.lifecycle.BasePresenter
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.launchOnUITryCatch

class GenresPresenter(
        private val getGenresUseCase: GetGenresUseCase
) : BasePresenter<GenresContract.IView>(),
        GenresContract.IPresenter, ICoroutinesManager, IKodi {

    override fun onViewAttached(view: GenresContract.IView) = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                when(val result = getGenresUseCase.execute()) {
                    is SResult.Success -> view().showItems(result.data)
                    is SResult.Error -> view().showError(result.message)
                }
            },
            catchBlock = {
                view().hideLoading()
            }
    )
}