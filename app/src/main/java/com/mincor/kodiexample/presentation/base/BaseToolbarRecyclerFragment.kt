package com.mincor.kodiexample.presentation.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController

abstract class BaseToolbarRecyclerFragment<I, D> : BaseLoadingRecyclerFragment<I, D>()
        where I  : BaseRecyclerUI<*>, D : BaseRecyclerDelegate<*, *> {

    protected open val toolbarTitle: String = ""
    protected open val toolbarView: Toolbar? = null

    protected open val needBackButton: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarView?.let { toolbar ->
            toolbar.title = toolbarTitle
            (activity as? AppCompatActivity)?.let { activityCompat ->
                activityCompat.setSupportActionBar(toolbar)
                if(needBackButton) {
                    activityCompat.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    activityCompat.supportActionBar?.setHomeButtonEnabled(true)
                    toolbar.setNavigationOnClickListener {
                        onBackClicked()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        toolbarView?.setNavigationOnClickListener(null)
        super.onDestroyView()
    }

    open fun onBackClicked() {
        this.findNavController().popBackStack()
    }
}