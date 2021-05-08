package com.mincor.kodiexample.presentation.base

import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.UnitHandler
import com.rasalexman.sticky.core.IStickyView
import com.rasalexman.sticky.core.IStickyViewOwner

interface IBaseStickyView : IStickyView {
    fun showAlertDialog(message: Any, okTitle: Int = R.string.title_try_again, okHandler: UnitHandler? = null)
    fun showToast(message: Any, interval: Int = 0)

    fun showLoading()
    fun hideLoading()
}