package com.mincor.kodiexample.presentation.details

import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.presentation.base.IBaseStickyView

@ExperimentalUnsignedTypes
interface IDetailsView : IBaseStickyView {
    fun showDetails(movieEntity: MovieEntity)
}