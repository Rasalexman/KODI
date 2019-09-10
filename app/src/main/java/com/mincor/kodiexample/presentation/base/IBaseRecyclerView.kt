package com.mincor.kodiexample.presentation.base

import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.kodiexample.mvp.base.lifecycle.IBaseView

interface IBaseRecyclerView<I : BaseRecyclerUI<*>> : IBaseView {

    fun showLoading()
    fun hideLoading()

    fun showItems(data: List<I>)
    fun addItems(list: List<AbstractItem<*>>)

    fun showError(message: String)
}