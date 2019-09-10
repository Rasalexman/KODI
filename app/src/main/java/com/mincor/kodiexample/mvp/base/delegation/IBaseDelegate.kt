package com.mincor.kodiexample.mvp.base.delegation

import androidx.lifecycle.Lifecycle
import com.mincor.kodiexample.mvp.base.lifecycle.IBasePresenter
import com.mincor.kodiexample.mvp.base.lifecycle.IBaseView

interface IBaseDelegate<out P>
        where P : IBasePresenter<out IBaseView> {
    val presenter: P
    fun onViewCreated(view: IBaseView, viewLifecycle: Lifecycle)
    fun onViewDestroy()
    fun delegate()
}

