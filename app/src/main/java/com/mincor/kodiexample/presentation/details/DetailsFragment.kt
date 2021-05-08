package com.mincor.kodiexample.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import coil.api.load
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.databinding.FragmentDetailsBinding
import com.mincor.kodiexample.presentation.base.BaseFragment
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.throwKodiException

class DetailsFragment : BaseFragment<DetailsPresenter>(),
        IDetailsView, IKodi {

    private lateinit var detailsBinding: FragmentDetailsBinding

    override val needBackButton: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.fragment_details

    override val toolbarView: Toolbar
        get() = detailsBinding.toolBarView

    override val contentLayout: View
        get() = detailsBinding.detailsContentLayout

    override val loadingLayout: View
        get() = detailsBinding.contentProgressBar

    override val presenter: DetailsPresenter get() = DetailsPresenter().apply {
        movieId = arguments?.getInt(KEY_MOVIE_ID) ?: throwKodiException<IllegalAccessException>("Movie Id can't be null")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            detailsBinding = FragmentDetailsBinding.bind(it)
        }
    }

    override fun showDetails(movieEntity: MovieEntity) {
        hideLoading()

        detailsBinding.backdropImageView.load(movieEntity.getBackDropImageUrl())
        detailsBinding.posterImageView.load(movieEntity.getImageUrl())
        detailsBinding.titleTextView.text = movieEntity.title
        detailsBinding.releaseTextView.text = Consts.UI_DATE_FORMATTER.format(movieEntity.releaseDate)
        detailsBinding.ratingTextView.text = getString(R.string.title_rating, movieEntity.voteAverage.toString())
        detailsBinding.overviewTextView.text = movieEntity.overview
    }

    override fun onDestroyView() {
        super.onDestroyView()
        detailsBinding.unbind()
    }

    companion object {
        const val KEY_MOVIE_ID = "movie_id"
    }
}