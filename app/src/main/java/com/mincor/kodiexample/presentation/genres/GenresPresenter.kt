package com.mincor.kodiexample.presentation.genres

import com.mincor.kodi.core.IKodi
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.genres.IGenresOutUseCase
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.launchOnUITryCatch
import com.rasalexman.sticky.core.IStickyPresenter

class GenresPresenter(
        private val getGenresUseCase: IGenresOutUseCase
) : IStickyPresenter<GenresContract.IView>,
        GenresContract.IPresenter, ICoroutinesManager, IKodi {

    override val mustRestoreSticky: Boolean
        get() = true

    override fun onViewCreated(view: GenresContract.IView) = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = getGenresUseCase.execute()
                view().sticky {
                    when(result) {
                        is SResult.Success -> showItems(result.data)
                        is SResult.Error -> showError(result.message)
                    }
                }
            },
            catchBlock = {
                view().hideLoading()
            }
    )
}