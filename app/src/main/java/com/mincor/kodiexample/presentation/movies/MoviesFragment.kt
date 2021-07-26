package com.mincor.kodiexample.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.unsafeLazy
import com.mincor.kodiexample.databinding.FragmentAppbarRecyclerBinding
import com.mincor.kodiexample.presentation.base.BaseRecyclerFragment
import com.mincor.kodiexample.presentation.details.DetailsFragment.Companion.KEY_MOVIE_ID
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.instance
import com.rasalexman.kodi.core.throwKodiException

@ExperimentalUnsignedTypes
class MoviesFragment : BaseRecyclerFragment<MovieUI, MoviesContract.IPresenter>(),
        MoviesContract.IView, IKodi {

    private lateinit var moviesBinding: FragmentAppbarRecyclerBinding

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

    override val toolbarView: Toolbar
        get() = moviesBinding.toolBarView

    override val toolbarTitle: String
        get() = arguments?.getString(KEY_GENRE_NAME).orEmpty()

    override val needBackButton: Boolean = true
    override val needScroll: Boolean = true

    override val onLoadNextHandler: (() -> Unit)?
            get() = presenter::getNextMoviesByGenreId

    override val onItemClickHandler: ((MovieUI) -> Unit) = { item ->
        this.findNavController()
                .navigate(
                        R.id.action_moviesFragment_to_detailsFragment,
                        bundleOf(KEY_MOVIE_ID to item.id)
                )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            moviesBinding = FragmentAppbarRecyclerBinding.bind(it)
        }
    }

    companion object {
        const val KEY_GENRE_NAME = "genre_name"
        const val KEY_GENRE_ID = "genre_id"
    }
}