package com.mincor.kodiexample.mvp.base.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.mincor.kodiexample.mvp.base.IBasePresenter
import com.mincor.kodiexample.mvp.base.IBaseView

interface IBaseLifecyclePresenter<V : IBaseView> : IBasePresenter<V>, LifecycleObserver {
    fun attachView(view: V, viewLifecycle: Lifecycle)
    fun addStickyContinuation(continuation: StickyContinuation<*>, block: V.(StickyContinuation<*>) -> Unit)
    fun removeStickyContinuation(continuation: StickyContinuation<*>): Boolean
}