package com.mincor.kodiexample.mvp.base.delegation

import androidx.lifecycle.Lifecycle
import com.mincor.kodiexample.mvp.base.lifecycle.IBaseView
import com.mincor.kodiexample.mvp.base.lifecycle.IBasePresenter

@Suppress("UNCHECKED_CAST")
abstract class BaseDelegate<V, out P>(override val presenter: P)
    : IBaseDelegate<P>, IBasePresenter<V> by presenter
        where V : IBaseView, P : IBasePresenter<V> {

    override fun onViewCreated(view: IBaseView, viewLifecycle: Lifecycle) {
        attachView(view as V, viewLifecycle)
        delegate()
    }

    override fun onViewDestroy() = Unit
}