package com.mincor.kodiexample.presentation.movies

import android.view.View
import coil.api.load
import com.mikepenz.fastadapter.FastAdapter
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.clear
import com.mincor.kodiexample.common.hide
import com.mincor.kodiexample.common.show
import com.mincor.kodiexample.presentation.base.BaseRecyclerUI
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_movies_item.view.*

data class MovieUI(
        val id: Int,
        val voteCount: Int,
        val voteAverage: Double,
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

    override val layoutRes: Int = R.layout.layout_movies_item
    override fun getViewHolder(v: View) = MovieViewHolder(v)
    override val type: Int = 1032

    init {
        identifier = id.toLong()
    }

    val fullPosterUrl: String
        get() = "${BuildConfig.IMAGES_URL}$posterPath"

    class MovieViewHolder(override val containerView: View) : FastAdapter.ViewHolder<MovieUI>(containerView), LayoutContainer {

        override fun bindView(item: MovieUI, payloads: List<Any>) {
            with(containerView) {
                titleTextView.text = item.title
                releaseTextView.text = item.releaseDate
                overviewTextView.text = item.overview
                setVoteAverage(item)

                movieImageView.load(item.fullPosterUrl) {
                    placeholder(R.drawable.ic_cloud_off_black_24dp)
                    target(onStart = {
                        imageProgressBar.show()
                    }, onSuccess = {
                        movieImageView.setImageDrawable(it)
                        imageProgressBar.hide(true)
                    }, onError = {
                        movieImageView.setImageResource(R.drawable.ic_cloud_off_black_24dp)
                        imageProgressBar.hide(true)
                    })
                }
            }
        }

        override fun unbindView(item: MovieUI) {
            with(containerView) {
                titleTextView.clear()
                releaseTextView.clear()
                overviewTextView.clear()
                voteAverageTextView.clear()
                movieImageView.clear()
                imageProgressBar.hide()
            }
        }

        private fun View.setVoteAverage(item: MovieUI) {
            if(item.voteAverage > 0.0) {
                voteAverageTextView.show()
                voteAverageTextView.text = item.voteAverage.toString()
            } else {
                voteAverageTextView.hide()
            }
        }
    }
}