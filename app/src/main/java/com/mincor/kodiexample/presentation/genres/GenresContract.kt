package com.mincor.kodiexample.presentation.genres

import com.mincor.kodiexample.presentation.base.IBaseRecyclerView
import com.rasalexman.sticky.core.IStickyPresenter

@ExperimentalUnsignedTypes
interface GenresContract {

    interface IView : IBaseRecyclerView<GenreItem>

    interface IPresenter : IStickyPresenter<IView> {
        fun loadGenres()
    }
}