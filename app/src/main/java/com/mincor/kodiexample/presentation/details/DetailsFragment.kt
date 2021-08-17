package com.mincor.kodiexample.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import coil.load
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.databinding.FragmentDetailsBinding
import com.mincor.kodiexample.presentation.base.BaseFragment
import com.rasalexman.kodi.core.IKodi
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.kodi.core.throwKodiException

@ExperimentalUnsignedTypes
class DetailsFragment : BaseFragment<IDetailsPresenter>(),
        IDetailsView, IKodi {

    private var detailsBinding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
            get() = detailsBinding ?: throwKodiException<NullPointerException>("FragmentDetailsBinding is null")

    override val needBackButton: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.fragment_details

    override val toolbarView: Toolbar
        get() = binding.toolBarView

    override val contentLayout: View
        get() = binding.detailsContentLayout

    override val loadingLayout: View
        get() = binding.contentProgressBar

    override val presenter: IDetailsPresenter by immutableInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            detailsBinding = FragmentDetailsBinding.bind(it)
        }
    }

    override fun showDetails(movieEntity: MovieEntity) {
        hideLoading()
        binding.apply {
            backdropImageView.load(movieEntity.getBackDropImageUrl())
            posterImageView.load(movieEntity.getImageUrl())
            titleTextView.text = movieEntity.title
            releaseTextView.text = Consts.UI_DATE_FORMATTER.format(movieEntity.releaseDate)
            ratingTextView.text = getString(R.string.title_rating, movieEntity.voteAverage.toString())
            overviewTextView.text = movieEntity.overview
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        detailsBinding?.unbind()
        detailsBinding = null
    }

    companion object {
        const val KEY_MOVIE_ID = "movie_id"
    }
}