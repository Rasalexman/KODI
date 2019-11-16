package com.mincor.kodiexample.presentation.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.mincor.kodiexample.R
import sun.jvm.hotspot.utilities.IntArray
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R




class MovieImageView : ImageView {

    private var heightProc: Float = DEFAULT_VALUE
    private var widthProc: Float = DEFAULT_VALUE

    private var heightAspect: String = ""
    private var widthAspect: String = ""

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
        widthProc = ta.getFloat(R.styleable.MovieImageView_widthProc, DEFAULT_VALUE)
        heightProc = ta.getFloat(R.styleable.MovieImageView_heightProc, DEFAULT_VALUE)

        widthAspect = ta.getString(R.styleable.MovieImageView_widthAspect) ?: ASPECT_4_3
        heightAspect = ta.getString(R.styleable.MovieImageView_heightAspect) ?: ASPECT_4_3
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val imageWidth: Int
        val imageHeight: Int
        when {
            ///-------> HEIGHT ASPECT
            heightAspect == ASPECT_4_3 -> {
                imageWidth = getWidthByDisplay(widthMeasureSpec)
                imageHeight = (imageWidth * 4) / 3
            }
            heightAspect == ASPECT_3_4 -> {
                imageWidth = getWidthByDisplay(widthMeasureSpec)
                imageHeight = (imageWidth * 3) / 4
            }
            heightAspect == ASPECT_16_9 -> {
                imageWidth = getWidthByDisplay(widthMeasureSpec)
                imageHeight = (imageWidth * 16) / 9
            }
            heightAspect == ASPECT_9_16 -> {
                imageWidth = getWidthByDisplay(widthMeasureSpec)
                imageHeight = (imageWidth * 9) / 16
            }
            ////-----> WIDTH ASPECT
            widthAspect == ASPECT_4_3 -> {
                imageHeight = getHeightByDisplay(heightMeasureSpec)
                imageWidth = (imageHeight * 4) / 3
            }
            widthAspect == ASPECT_3_4 -> {
                imageHeight = getHeightByDisplay(heightMeasureSpec)
                imageWidth = (imageHeight * 3) / 4
            }
            widthAspect == ASPECT_16_9 -> {
                imageHeight = getHeightByDisplay(heightMeasureSpec)
                imageWidth = (imageHeight * 16) / 9
            }
            widthAspect == ASPECT_9_16 -> {
                imageHeight = getHeightByDisplay(heightMeasureSpec)
                imageWidth = (imageHeight * 9) / 16
            }
            else -> {
                imageWidth = widthMeasureSpec
                imageHeight = heightMeasureSpec
            }
        }

        setMeasuredDimension(imageWidth, imageHeight)
    }

    private fun getWidthByDisplay(widthMeasureSpec: Int): Int {
        return widthMeasureSpec.takeIf { widthProc == DEFAULT_VALUE } ?: ((context.resources.displayMetrics.widthPixels * widthProc).toInt())
    }

    private fun getHeightByDisplay(heightMeasureSpec: Int): Int {
        return heightMeasureSpec.takeIf { heightProc == DEFAULT_VALUE } ?: ((context.resources.displayMetrics.heightPixels * heightProc).toInt())
    }

    

    companion object {
        private const val ASPECT_4_3 = "0"
        private const val ASPECT_3_4 = "1"
        private const val ASPECT_16_9 = "2"
        private const val ASPECT_9_16 = "3"

        private const val DEFAULT_VALUE = 1f
    }
}