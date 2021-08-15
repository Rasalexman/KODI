package com.mincor.kodiexample.presentation.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.mincor.kodiexample.R
import com.mincor.kodiexample.common.UnitHandler
import com.mincor.kodiexample.common.hide
import com.mincor.kodiexample.common.show
import com.mincor.sticky.presentation.base.INavigationHandler
import com.rasalexman.sticky.base.StickyFragment
import com.rasalexman.sticky.core.IStickyPresenter
import com.rasalexman.sticky.core.IStickyView

@ExperimentalUnsignedTypes
abstract class BaseFragment<P : IStickyPresenter<out IStickyView>> : StickyFragment<P>(),
    INavigationHandler, Toolbar.OnMenuItemClickListener {

    private var alertDialog: Dialog? = null

    open val loadingLayout: View? = null
    open val contentLayout: View? = null

    protected open val toolbarTitle: String = ""
    protected open val toolbarView: Toolbar? = null
    protected open val toolBarMenuResId: Int? = null

    protected open val needBackButton: Boolean = false

    override val currentNavHandler: INavigationHandler?
        get() = this

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setHasOptionsMenu(toolBarMenuResId != null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarView?.let { toolbar ->
            // title
            if(toolbarTitle.isNotEmpty()) toolbar.title = toolbarTitle

            toolBarMenuResId?.let {
                toolbar.menu?.clear()
                toolbar.inflateMenu(it)
                toolbar.setOnMenuItemClickListener(this)
            }

            if(needBackButton) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
                toolbar.setNavigationOnClickListener {
                    onBackClicked()
                }
            }

            // activity
            /*(activity as? AppCompatActivity)?.let { activityCompat ->
                activityCompat.setSupportActionBar(toolbar)

            }*/


        }
    }

    open fun showAlertDialog(message: Any, okTitle: Int = R.string.title_try_again, okHandler: UnitHandler? = null) {

        context?.let { liveContext ->
            hideLoading()
            closeAlertDialog()

            alertDialog = AlertDialog
                .Builder(liveContext)
                .setTitle(R.string.title_warning)
                .setCancelable(false)
                .setPositiveButton(okTitle) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    okHandler?.invoke()
                }
                .setNegativeButton(R.string.title_cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.run {
                    when(message) {
                        is String -> setMessage(message)
                        is Int -> setMessage(message)
                        else -> setMessage(message.toString())
                    }
                    show()
                }
        }
    }

    open fun showToast(message: Any, interval: Int = Toast.LENGTH_SHORT) {
        context?.let { liveContext ->
            hideLoading()
            when(message) {
                is String -> Toast.makeText(liveContext, message, interval).show()
                is Int -> Toast.makeText(liveContext, message, interval).show()
                else -> Toast.makeText(liveContext, message.toString(), interval).show()
            }
        }
    }

    open fun showLoading() {
        contentLayout?.hide()
        loadingLayout?.show()
    }
    open fun hideLoading() {
        loadingLayout?.hide()
        contentLayout?.show()
    }

    override fun onBackPressed(): Boolean {
        val backStackEntryCount = this.childFragmentManager.backStackEntryCount
        return backStackEntryCount == 0
    }

    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return true
    }

    @ExperimentalUnsignedTypes
    override fun onDestroyView() {
        toolbarView?.setNavigationOnClickListener(null)
        closeAlertDialog()
        super.onDestroyView()
    }

    open fun onBackClicked() {
        this.findNavController().popBackStack()
    }

    private fun closeAlertDialog() {
        alertDialog?.dismiss()
        alertDialog = null
    }
}