package com.mincor.kodiexample.data.model.ui.genres

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mincor.kodiexample.data.model.ui.base.BaseRecyclerUI

data class GenreUI(val id: Int, val name: String) : BaseRecyclerUI<GenreUI.GenreViewHolder>() {

    init {
        identifier = id.toLong()
    }

    override fun getViewHolder(v: View) = GenreViewHolder(v)

    class GenreViewHolder(view: View) : FastAdapter.ViewHolder<GenreUI>(view) {

        //private val titleTextView = view.find<TextView>(R.id.title_text_view_id)

        override fun bindView(item: GenreUI, payloads: MutableList<Any>) {
            //titleTextView.text = item.name
        }

        override fun unbindView(item: GenreUI) {
            //titleTextView.clear()
        }
    }
}

