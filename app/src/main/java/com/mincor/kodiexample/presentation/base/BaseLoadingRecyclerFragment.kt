package com.mincor.kodiexample.presentation.base

import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.kodiexample.mvp.base.delegation.BaseDelegationFragment

abstract class BaseLoadingRecyclerFragment<I, D> : BaseDelegationFragment<D>(),
        IBaseRecyclerView<I>
        where I  : BaseRecyclerUI<*>, D : BaseRecyclerDelegate<*, *> {


    override fun showLoading() {
        delegate.showLoading()
    }

    override fun hideLoading() {
        delegate.hideLoading()
    }

    override fun addItems(list: List<AbstractItem<*>>) {
        delegate.addItems(list)
    }

    override fun showItems(data: List<I>) {
        delegate.showItems(data)
    }

    override fun showError(message: String) {
        delegate.showError(message)
    }

}