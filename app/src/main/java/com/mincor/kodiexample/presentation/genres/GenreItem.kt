package com.mincor.kodiexample.presentation.genres

import android.view.View
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.core.view.forEachIndexed
import coil.dispose
import coil.load
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItemVHFactory
import com.mikepenz.fastadapter.items.BaseItem
import com.mikepenz.fastadapter.items.BaseItemFactory
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.R
import com.mincor.kodiexample.databinding.LayoutGenreItemBinding
import com.rasalexman.coroutinesmanager.ICoroutinesManager

@Keep
data class GenreItem(
        val id: Int,
        val name: String,
        val images: List<String>
) : BaseItem<GenreItem.GenreViewHolder>() {

    override var identifier: Long = id.toLong()
    override val type: Int = 1024
    override val factory: IItemVHFactory<GenreViewHolder> = GenreItemFactory

    object GenreItemFactory : BaseItemFactory<GenreViewHolder>() {
        override val layoutRes: Int get() = R.layout.layout_genre_item
        override fun getViewHolder(v: View) = GenreViewHolder(v)
    }

    class GenreViewHolder(containerView: View) : FastAdapter.ViewHolder<GenreItem>(containerView), ICoroutinesManager {

        private val itemBinding = LayoutGenreItemBinding.bind(itemView)

        override fun bindView(item: GenreItem, payloads: List<Any>) {
            with(itemBinding) {
                titleTextView.text = item.name
                val urlList = item.images.toList()
                imagesLayout.forEachIndexed { index, view ->
                    urlList.getOrNull(index)?.let { remoteUrl ->
                        val imageView = view as ImageView
                        val imageUrl = "${BuildConfig.IMAGES_URL}${remoteUrl}"
                        imageView.load(imageUrl)
                    }
                }
            }
        }

        override fun unbindView(item: GenreItem) {
            itemBinding.apply {
                titleTextView.text = null
                imageView1.dispose()
                imageView2.dispose()
                imageView3.dispose()
            }
        }
    }
}