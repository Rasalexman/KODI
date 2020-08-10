package com.mincor.kodiexample.presentation.movies

import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.instance
import com.rasalexman.kodi.core.throwKodiException
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.unsafeLazy
import com.mincor.kodiexample.presentation.base.BaseRecyclerFragment
import com.mincor.kodiexample.presentation.details.DetailsFragment.Companion.KEY_MOVIE_ID
import kotlinx.android.synthetic.main.fragment_appbar_recycler.*

class MoviesFragment : BaseRecyclerFragment<MovieUI, MoviesContract.IPresenter>(),
        MoviesContract.IView, IKodi {

    override val recyclerViewId: Int
        get() = R.id.recyclerView

    override val layoutId: Int
        get() = R.layout.fragment_appbar_recycler

    override val presenter: MoviesContract.IPresenter by unsafeLazy {
        instance<MoviesContract.IPresenter>().apply {
            genreId = arguments?.getInt(KEY_GENRE_ID)
                    ?: throwKodiException<IllegalStateException>("Genre Id can't be null")
        }
    }

    override val toolbarView: Toolbar?
        get() = toolBarView

    override val toolbarTitle: String
        get() = arguments?.getString(KEY_GENRE_NAME).orEmpty()

    override val needBackButton: Boolean = true
    override val needScroll: Boolean = true

    override val onLoadNextHandler: (() -> Unit)?
            get() = presenter::getNextMoviesByGenreId

    override val onItemClickHandler: ((MovieUI) -> Unit)? = { item ->
        this.findNavController()
                .navigate(
                        R.id.action_moviesFragment_to_detailsFragment,
                        bundleOf(KEY_MOVIE_ID to item.id)
                )
    }

    companion object {
        const val KEY_GENRE_NAME = "genre_name"
        const val KEY_GENRE_ID = "genre_id"
    }
}