package com.mincor.kodiexample.presentation.movies

import com.mincor.kodiexample.R
import com.mincor.kodiexample.presentation.base.BaseRecyclerDelegate
import com.mincor.kodiexample.presentation.movies.MoviesFragment.Companion.KEY_GENRE_ID

class MoviesDelegate(presenter: MoviesContract.IPresenter) :
        BaseRecyclerDelegate<MoviesContract.IView, MoviesContract.IPresenter>(presenter),
        MoviesContract.IDelegate {

    override val recyclerViewId: Int = R.id.recyclerView

    override val fragment: MoviesFragment?
        get() = unsafeView as? MoviesFragment

    private var genreId: Int? = null

    override fun delegate() {
        super.delegate()
        genreId = fragment?.arguments?.getInt(KEY_GENRE_ID)
        setRVCScroll()
        presenter.getMoviesByGenreId(genreId)
    }

    override fun onLoadNextPageHandler(page: Int) {
        presenter.getNextMoviesByGenreId(genreId)
    }

}