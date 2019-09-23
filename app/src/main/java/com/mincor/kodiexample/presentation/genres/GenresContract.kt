package com.mincor.kodiexample.presentation.genres

import com.mincor.kodiexample.presentation.base.IBaseRecyclerView
import com.rasalexman.sticky.core.IStickyPresenter

interface GenresContract {

    interface IView : IBaseRecyclerView<GenreUI>

    interface IPresenter : IStickyPresenter<IView>
}