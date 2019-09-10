package com.mincor.kodiexample.presentation.movies

import com.mincor.kodiexample.mvp.base.delegation.IBaseDelegate
import com.mincor.kodiexample.mvp.base.lifecycle.IBasePresenter
import com.mincor.kodiexample.presentation.base.IBaseRecyclerView

interface MoviesContract {

    interface IView : IBaseRecyclerView<MovieUI>

    interface IPresenter : IBasePresenter<IView> {
        fun getMoviesByGenreId(genreId: Int?)
        fun getNextMoviesByGenreId(genreId: Int?)
    }

    interface IDelegate : IBaseDelegate<IPresenter>
}