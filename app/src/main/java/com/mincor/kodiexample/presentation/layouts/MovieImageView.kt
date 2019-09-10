package com.mincor.kodiexample.presentation.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class MovieImageView : ImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val imageWidth: Int = (context.resources.displayMetrics.widthPixels * 0.4).toInt()
        val imageHeight: Int = (imageWidth * 4) / 3
        setMeasuredDimension(imageWidth, imageHeight)
    }
}