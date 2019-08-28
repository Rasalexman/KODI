package com.mincor.kodiexample.mvp.base.delegation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mincor.kodiexample.mvp.base.IBasePresenter
import com.mincor.kodiexample.mvp.base.IBaseView

abstract class BaseDelegationFragment<D> : Fragment(), IBaseView
        where D : IBaseDelegate<IBasePresenter<out IBaseView>> {

    /**
     * Layout Resource Id
     */
    protected abstract val layoutResId: Int

    /**
     * Delegate
     */
    protected abstract val delegate: D

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delegate.onViewCreated(this, this.lifecycle)
    }

    override fun onDestroyView() {
        delegate.onViewDestroyed()
        super.onDestroyView()
    }
}