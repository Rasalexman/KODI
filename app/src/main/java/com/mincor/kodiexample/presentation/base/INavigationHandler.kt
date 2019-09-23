package com.mincor.sticky.presentation.base

interface INavigationHandler {
    val currentNavHandler: INavigationHandler?
    fun onSupportNavigateUp(): Boolean
    fun onBackPressed(): Boolean
}