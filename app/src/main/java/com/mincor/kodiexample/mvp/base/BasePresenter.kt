package com.mincor.kodiexample.mvp.base

abstract class BasePresenter<V : IBaseView> : IBasePresenter<V> {
    override var view: V? = null
    override fun attachView(view: V) {
        this.view = view
        onViewAttached(view)
    }

    override fun detachView() = Unit

    protected open fun onViewAttached(view: V) {
        // Nothing to do here. This is an event handled by the subclasses.
    }
}