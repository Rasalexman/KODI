package com.mincor.kodiexample.presentation.genres

import android.graphics.Rect
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.immutableInstance
import com.mincor.kodiexample.R
import com.mincor.kodiexample.presentation.base.BaseRecyclerFragment
import com.mincor.kodiexample.presentation.movies.MoviesFragment

class GenresFragment : BaseRecyclerFragment<GenreUI, GenresContract.IPresenter>(),
        GenresContract.IView, IKodi {

    override val recyclerViewId: Int
        get() = R.id.recyclerView

    override val layoutId: Int
        get() = R.layout.fragment_appbar_recycler

    override val presenter: GenresContract.IPresenter by immutableInstance()

    override var itemDecoration: RecyclerView.ItemDecoration? = null
        get() = field ?: let {
            field = object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.bottom = parent.context.resources.getDimensionPixelOffset(R.dimen.size_4dp)
                }
            }
            field
        }

    override val onItemClickHandler: ((GenreUI) -> Unit)? = { item ->
        this.findNavController().navigate(
                R.id.action_genresFragment_to_moviesFragment,
                bundleOf(
                        MoviesFragment.KEY_GENRE_ID to item.id,
                        MoviesFragment.KEY_GENRE_NAME to item.name
                )
        )
    }
}