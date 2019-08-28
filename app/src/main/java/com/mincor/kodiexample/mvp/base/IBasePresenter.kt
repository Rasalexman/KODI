package com.mincor.kodiexample.mvp.base

interface IBasePresenter<V : IBaseView> {
    var view:V?
    fun attachView(view: V)
    fun detachView()
}