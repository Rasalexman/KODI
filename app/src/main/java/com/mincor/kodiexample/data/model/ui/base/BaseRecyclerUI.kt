package com.mincor.kodiexample.data.model.ui.base

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

abstract class BaseRecyclerUI<VH> :
    AbstractItem<VH>() where VH : FastAdapter.ViewHolder<*> {
    override val layoutRes: Int = -1
    override val type: Int = 1
}