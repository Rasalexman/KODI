package com.mincor.kodiexample.mvp.base.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

interface IBasePresenter<V : IBaseView> : LifecycleObserver {
    var unsafeView: V?
    fun attachView(view: V, viewLifecycle: Lifecycle)
    fun removeStickyContinuation(continuation: StickyContinuation<*>): Boolean
}