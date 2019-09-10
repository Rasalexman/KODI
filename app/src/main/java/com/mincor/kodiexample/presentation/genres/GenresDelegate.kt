package com.mincor.kodiexample.presentation.genres

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.kodiexample.R
import com.mincor.kodiexample.presentation.base.BaseRecyclerDelegate
import com.mincor.kodiexample.presentation.movies.MoviesFragment.Companion.KEY_GENRE_ID
import com.mincor.kodiexample.presentation.movies.MoviesFragment.Companion.KEY_GENRE_NAME

class GenresDelegate(presenter: GenresContract.IPresenter) :
        BaseRecyclerDelegate<GenresContract.IView, GenresContract.IPresenter>(presenter),
        GenresContract.IDelegate {

    override val recyclerViewId: Int = R.id.recyclerView

    override var itemDecoration: RecyclerView.ItemDecoration? = null
        get() = field ?: let {
            field = object  : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                            super.getItemOffsets(outRect, view, parent, state)
                            outRect.bottom = parent.context.resources.getDimensionPixelOffset(R.dimen.size_4dp)
                        }
                    }
            field
        }

    override fun onItemClickHandler(item: AbstractItem<*>, position: Int) {
        val itemGenre = item as GenreUI
        fragment?.findNavController()?.navigate(
                R.id.action_genresFragment_to_moviesFragment,
                Bundle().apply {
                    putInt(KEY_GENRE_ID, itemGenre.id)
                    putString(KEY_GENRE_NAME, itemGenre.name)
                }
        )
    }

    override val fragment: GenresFragment?
            get() = view as? GenresFragment

}