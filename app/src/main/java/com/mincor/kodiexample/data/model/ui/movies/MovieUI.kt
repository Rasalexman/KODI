package com.mincor.kodiexample.data.model.ui.movies

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.data.model.ui.base.BaseRecyclerUI

data class MovieUI(
    val id: Int,
    val voteCount: Int,
    val isVideo: Boolean,
    val title: String,
    val popularity: Double,
    val posterPath: String,
    val originalLanguage: String,
    val originalTitle: String,
    val genreIds: List<Int>,
    val backdropPath: String,
    val releaseDate: String,
    val adult: Boolean,
    val overview: String
) : BaseRecyclerUI<MovieUI.MovieViewHolder>() {

    override fun getViewHolder(v: View) = MovieViewHolder(v)

    init {
        identifier = id.toLong()
    }

    val fullPosterUrl: String
        get() = "${BuildConfig.IMAGES_URL}$posterPath"

    class MovieViewHolder(view: View) : FastAdapter.ViewHolder<MovieUI>(view) {

        //private val titleTextView = view.find<TextView>(R.id.title_text_view_id)

        override fun bindView(item: MovieUI, payloads: MutableList<Any>) {
            //titleTextView.text = item.name
        }

        override fun unbindView(item: MovieUI) {
            //titleTextView.clear()
        }
    }
}