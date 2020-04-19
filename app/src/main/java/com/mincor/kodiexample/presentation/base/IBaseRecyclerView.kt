package com.mincor.kodiexample.presentation.base

import com.mikepenz.fastadapter.IItem

interface IBaseRecyclerView<I : IItem<*>> : IBaseStickyView {

    fun showItems(list: List<I>)
    fun addItems(list: List<I>)

    fun showError(message: String?)
}