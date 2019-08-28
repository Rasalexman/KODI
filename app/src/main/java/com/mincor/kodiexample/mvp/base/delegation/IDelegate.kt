package com.mincor.kodiexample.mvp.base.delegation

import com.mincor.kodiexample.mvp.base.IBasePresenter
import com.mincor.kodiexample.mvp.base.IBaseView

interface IDelegate<V : IBaseView, P : IBasePresenter<V>, D : IBaseDelegate<P>> {
    val delegate: D
}