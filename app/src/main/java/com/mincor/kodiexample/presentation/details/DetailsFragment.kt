package com.mincor.kodiexample.presentation.details

import android.view.View
import androidx.appcompat.widget.Toolbar
import coil.api.load
import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.throwKodiException
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : BaseFragment<DetailsPresenter>(),
        IDetailsView, IKodi {

    override val needBackButton: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.fragment_details

    override val toolbarView: Toolbar?
        get() = toolBarView

    override val contentLayout: View?
        get() = detailsContentLayout

    override val loadingLayout: View?
        get() = contentProgressBar

    override val presenter: DetailsPresenter get() = DetailsPresenter().apply {
        movieId = arguments?.getInt(KEY_MOVIE_ID) ?: throwKodiException<IllegalAccessException>("Movie Id can't be null")
    }

    override fun showDetails(movieEntity: MovieEntity) {
        hideLoading()

        backdropImageView.load(movieEntity.getBackDropImageUrl())
        posterImageView.load(movieEntity.getImageUrl())
        titleTextView.text = movieEntity.title
        releaseTextView.text = Consts.UI_DATE_FORMATTER.format(movieEntity.releaseDate)
        ratingTextView.text = getString(R.string.title_rating, movieEntity.voteAverage.toString())
        overviewTextView.text = movieEntity.overview
    }

    companion object {
        const val KEY_MOVIE_ID = "movie_id"
    }
}