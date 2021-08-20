package com.mincor.kodiexample.presentation.genres

import com.mincor.kodiexample.presentation.base.IBaseRecyclerView
import com.rasalexman.kodispatcher.IKodiListener
import com.rasalexman.sticky.core.IStickyPresenter

interface GenresContract {

    interface IView : IBaseRecyclerView<GenreItem>

    interface IPresenter : IStickyPresenter<IView>, IKodiListener {

    }
}