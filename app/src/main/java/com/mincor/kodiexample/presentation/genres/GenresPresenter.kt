package com.mincor.kodiexample.presentation.genres

import com.mincor.kodiexample.common.Consts.Modules.PresentersName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.genres.IGenresOutUseCase
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.launchOnUITryCatch
import com.rasalexman.kodi.annotations.BindSingle
import com.rasalexman.kodi.annotations.IgnoreInstance
import com.rasalexman.sticky.core.IStickyPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ExperimentalUnsignedTypes
@BindSingle(
        toClass = GenresContract.IPresenter::class,
        toModule = PresentersName
)
class GenresPresenter constructor(
        private val getGenresUseCase: IGenresOutUseCase
) : IStickyPresenter<GenresContract.IView>,
        GenresContract.IPresenter, ICoroutinesManager {

    override val mustRestoreSticky: Boolean
        get() = true

    override fun onViewCreated(view: GenresContract.IView) = launchOnUITryCatch(
            tryBlock = {
                view().showLoading()
                val result = withContext(Dispatchers.IO) { getGenresUseCase.invoke() }
                view().sticky {
                    when(result) {
                        is SResult.Success -> showItems(result.data)
                        is SResult.Error -> showError(result.message)
                    }
                }
            },
            catchBlock = {
                println("----> ERROR = $it")
                view().hideLoading()
            }
    )
}