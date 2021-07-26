package com.mincor.kodiexample.presentation.movies

import com.mincor.kodiexample.presentation.base.IBaseRecyclerView
import com.rasalexman.sticky.core.IStickyPresenter

@ExperimentalUnsignedTypes
interface MoviesContract {

    interface IView : IBaseRecyclerView<MovieUI>

    interface IPresenter : IStickyPresenter<IView> {
        var genreId: Int
        fun getNextMoviesByGenreId()
    }
}