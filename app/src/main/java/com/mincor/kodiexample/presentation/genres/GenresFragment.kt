package com.mincor.kodiexample.presentation.genres

import com.mincor.kodi.core.IKodi
import com.mincor.kodi.core.immutableInstance
import com.mincor.kodiexample.R
import com.mincor.kodiexample.presentation.base.BaseLoadingRecyclerFragment

class GenresFragment : BaseLoadingRecyclerFragment<GenreUI, GenresDelegate>(),
        GenresContract.IView, IKodi {

    override val layoutResId: Int = R.layout.fragment_appbar_recycler
    override val delegate: GenresDelegate by immutableInstance()
}