package com.mincor.kodiexample.presentation.genres

import android.view.View
import android.widget.ImageView
import androidx.core.view.forEachIndexed
import coil.api.load
import com.mikepenz.fastadapter.FastAdapter
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.clear
import com.mincor.kodiexample.presentation.base.BaseRecyclerUI
import kotlinx.android.synthetic.main.layout_genre_item.view.*

data class GenreUI(
        val id: Int,
        val name: String,
        val images: List<String>
) : BaseRecyclerUI<GenreUI.GenreViewHolder>() {

    init {
        identifier = id.toLong()
    }

    override val layoutRes: Int = R.layout.layout_genre_item
    override fun getViewHolder(v: View) = GenreViewHolder(v)

    class GenreViewHolder(view: View) : FastAdapter.ViewHolder<GenreUI>(view) {

        override fun bindView(item: GenreUI, payloads: MutableList<Any>) {
            with(itemView) {
                titleTextView.text = item.name

                val urlList = item.images
                imagesLayout.forEachIndexed { index, view ->
                    val imageView = view as ImageView
                    val imageUrl = "${BuildConfig.IMAGES_URL}${urlList[index]}"
                    imageView.load(imageUrl)
                }
            }
        }

        override fun unbindView(item: GenreUI) {
            with(itemView) {
                titleTextView.text = null
                imageView1.clear()
                imageView2.clear()
                imageView3.clear()
            }
        }
    }
}

