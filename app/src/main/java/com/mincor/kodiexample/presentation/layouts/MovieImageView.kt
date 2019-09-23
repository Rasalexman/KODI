package com.mincor.kodiexample.presentation.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.mincor.kodiexample.R


class MovieImageView : ImageView {

    private var heightProc: Float = 1f
    private var widthValue: Float = 0.4f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(attrs)
    }

    private fun initLayout(set: AttributeSet?) {
        if (set == null) {
            return
        }

        val ta = context.obtainStyledAttributes(set, R.styleable.MovieImageView)
        widthValue = ta.getFloat(R.styleable.MovieImageView_widthProc, 0.4f)
        heightProc = ta.getFloat(R.styleable.MovieImageView_heightProc, 1f)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val imageWidth: Int = (context.resources.displayMetrics.widthPixels * widthValue).toInt()
        val imageHeight: Int = (imageWidth * 4) / 3
        setMeasuredDimension(imageWidth, imageHeight)
    }
}