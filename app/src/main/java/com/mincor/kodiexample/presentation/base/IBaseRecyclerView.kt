package com.mincor.kodiexample.presentation.base

interface IBaseRecyclerView<I : BaseRecyclerUI<*>> : IBaseStickyView {

    fun showItems(list: List<I>)
    fun addItems(list: List<I>)

    fun showError(message: String?)
}