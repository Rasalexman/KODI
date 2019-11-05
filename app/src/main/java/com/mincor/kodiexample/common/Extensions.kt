package com.mincor.kodiexample.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.errorResult
import com.mincor.kodiexample.data.dto.successResult
import retrofit2.Response

data class ScrollPosition(var index: Int = 0, var top: Int = 0) {
    fun drop() {
        index = 0
        top = 0
    }
}

inline fun <reified T, reified R> R.unsafeLazy(noinline init: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, init)

inline fun <reified T : Fragment> T.toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this.context, message, duration).show()

fun TextView.clear() {
    this.text = null
    this.setOnClickListener(null)
}

fun ImageView.clear(isOnlyImage: Boolean = false) {
    this.setImageResource(0)
    this.setImageBitmap(null)
    this.setImageDrawable(null)
    if (isOnlyImage) this.setOnClickListener(null)
}

fun View.hide(gone: Boolean = true) {
    visibility = if (gone) View.GONE else View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

inline fun <reified T : Any, reified O : Any> Response<T>.getResult(alsoAction: (T) -> O): SResult<O> {
    return this.body()?.run {
        successResult(alsoAction(this))
    } ?: this.errorBody()?.let {
        errorResult(this.code(), this.message())
    } ?: emptyResult()
}