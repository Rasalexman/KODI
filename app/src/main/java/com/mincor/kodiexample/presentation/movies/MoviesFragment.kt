package com.mincor.kodiexample.presentation.movies


import androidx.appcompat.widget.Toolbar
import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.immutableInstance
import com.mincor.kodiexample.R
import com.mincor.kodiexample.presentation.base.BaseToolbarRecyclerFragment
import kotlinx.android.synthetic.main.fragment_movies.*

class MoviesFragment : BaseToolbarRecyclerFragment<MovieUI, MoviesDelegate>(),
        MoviesContract.IView, IKodi {

    override val layoutResId: Int = R.layout.fragment_appbar_recycler

    override val toolbarView: Toolbar?
        get() = toolBarView

    override val toolbarTitle: String
        get() = arguments?.getString(KEY_GENRE_NAME).orEmpty()

    override val needBackButton: Boolean = true

    override val delegate: MoviesDelegate by immutableInstance()

    companion object {
        const val KEY_GENRE_NAME = "genre_name"
        const val KEY_GENRE_ID = "genre_id"
    }
}
