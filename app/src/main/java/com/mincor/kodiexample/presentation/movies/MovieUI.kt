package com.mincor.kodiexample.presentation.movies

import android.view.View
import coil.load
import com.mikepenz.fastadapter.FastAdapter
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.clear
import com.mincor.kodiexample.common.hide
import com.mincor.kodiexample.common.show
import com.mincor.kodiexample.databinding.LayoutMoviesItemBinding
import com.mincor.kodiexample.presentation.base.BaseRecyclerUI

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

    class MovieViewHolder(containerView: View) : FastAdapter.ViewHolder<MovieUI>(containerView) {

        private val movieBinding: LayoutMoviesItemBinding = LayoutMoviesItemBinding.bind(containerView)

        override fun bindView(item: MovieUI, payloads: List<Any>) {
            with(movieBinding) {
                titleTextView.text = item.title
                releaseTextView.text = item.releaseDate
                overviewTextView.text = item.overview
                movieBinding.setVoteAverage(item)

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
            with(movieBinding) {
                titleTextView.clear()
                releaseTextView.clear()
                overviewTextView.clear()
                voteAverageTextView.clear()
                movieImageView.clear()
                imageProgressBar.hide()
            }
        }

        private fun LayoutMoviesItemBinding.setVoteAverage(item: MovieUI) {
            if(item.voteAverage > 0.0) {
                voteAverageTextView.show()
                voteAverageTextView.text = item.voteAverage.toString()
            } else {
                voteAverageTextView.hide()
            }
        }
    }
}