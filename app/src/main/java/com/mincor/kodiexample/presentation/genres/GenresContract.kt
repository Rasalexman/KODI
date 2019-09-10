package com.mincor.kodiexample.presentation.genres

import com.mincor.kodiexample.mvp.base.delegation.IBaseDelegate
import com.mincor.kodiexample.mvp.base.lifecycle.IBasePresenter
import com.mincor.kodiexample.mvp.base.lifecycle.IBaseView
import com.mincor.kodiexample.presentation.base.IBaseRecyclerView

interface GenresContract {

    interface IView : IBaseRecyclerView<GenreUI>

    interface IPresenter : IBasePresenter<IView>

    interface IDelegate : IBaseDelegate<IPresenter>
}