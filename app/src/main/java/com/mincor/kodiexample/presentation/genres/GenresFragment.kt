package com.mincor.kodiexample.presentation.genres

import android.graphics.Rect
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mincor.kodiexample.R
import com.mincor.kodiexample.presentation.base.BaseRecyclerFragment
import com.mincor.kodiexample.presentation.movies.MoviesFragment
import com.rasalexman.kodi.core.*
import com.rasalexman.kodispatcher.IKodiListener
import com.rasalexman.kodispatcher.notifyListener
import kotlin.random.Random


class GenresFragment : BaseRecyclerFragment<GenreItem, GenresContract.IPresenter>(),
        GenresContract.IView, IKodiListener {

    override val recyclerViewId: Int
        get() = R.id.recyclerView

    override val layoutId: Int
        get() = R.layout.fragment_appbar_recycler

    override val toolBarMenuResId: Int
        get() = R.menu.menu_refresh

    override val toolbarView: Toolbar?
        get() = view?.findViewById(R.id.toolBarView)

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

    override val onItemClickHandler: ((GenreItem) -> Unit) = { item ->
        this.findNavController().navigate(
                R.id.action_genresFragment_to_moviesFragment,
                bundleOf(
                        MoviesFragment.KEY_GENRE_ID to item.id,
                        MoviesFragment.KEY_GENRE_NAME to item.name
                )
        )
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if(item.itemId == R.id.actionRefresh) {
            notifyListener(com.rasalexman.kodispatcher.KodiEvent.INSTANCE, "Hello")
        } else if(item.itemId == R.id.actionNext) {
            val rand = Random.nextInt(0, mFastItemAdapter.itemCount) //.getItem()
            val randomItem = mFastItemAdapter.getItem(rand)
            onItemClickHandler(randomItem as GenreItem)
        }
        return super.onMenuItemClick(item)
    }
}