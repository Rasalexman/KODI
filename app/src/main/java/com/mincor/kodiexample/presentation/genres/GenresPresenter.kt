package com.mincor.kodiexample.presentation.genres

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.common.Consts.Modules.PresentersName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.genres.IGenresOutUseCase
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.coroutinesmanager.launchOnUITryCatch
import com.rasalexman.kodi.annotations.BindSingle
import com.rasalexman.kodi.annotations.WithInstance
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.instance
import com.rasalexman.kodispatcher.KodiEvent
import com.rasalexman.kodispatcher.addListener
import com.rasalexman.sticky.core.IStickyPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@ExperimentalUnsignedTypes
@BindSingle(
    toClass = GenresContract.IPresenter::class,
    toModule = PresentersName
)
class GenresPresenter constructor(
    @WithInstance(
        //scope = Consts.Scopes.GENRES,
        tag = Consts.Tags.GENRE_USE_CASE,
        with = IGenresOutUseCase::class
    )
    private val getGenresUseCase: IGenresOutUseCase
) : IStickyPresenter<GenresContract.IView>,
    GenresContract.IPresenter, ICoroutinesManager, IKodi {

    override val mustRestoreSticky: Boolean
        get() = true

    init {
        addListener<String>(KodiEvent.INSTANCE) {
            loadGenres()
        }
    }

    override fun onViewCreated(view: GenresContract.IView) {
        startLoadGenres(getGenresUseCase)
    }

    override fun loadGenres() {
        asyncJob.cancelChildren()
        val loadGenres = instance<IGenresOutUseCase>()
        startLoadGenres(loadGenres)
    }

    private fun startLoadGenres(localGetGenresUseCase: IGenresOutUseCase) {
        launchOnUITryCatch(
            tryBlock = {
                view().sticky {
                    hideLoading()
                    clearAdapter()
                    showLoading()
                }
                //val getGenresUseCase: IGenresOutUseCase = instance()
                val result = withContext(Dispatchers.IO) { localGetGenresUseCase.invoke() }
                delay(2000L)

                view().sticky {
                    when (result) {
                        is SResult.Success -> showItems(result.data)
                        is SResult.Error -> showError(result.message)
                        else -> Unit
                    }
                }
            },
            catchBlock = {
                println("----> ERROR = $it")
                view().hideLoading()
                view().showError(it.message.orEmpty())
            }
        )
    }
}