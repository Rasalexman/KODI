package com.mincor.kodiexample.presentation.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.mincor.kodiexample.R


class MovieImageView : ImageView {

    private var heightProc: Float = 1f
    private var widthProc: Float = 1f

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
        widthProc = ta.getFloat(R.styleable.MovieImageView_widthProc, 1f)
        heightProc = ta.getFloat(R.styleable.MovieImageView_heightProc, 1f)

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
                imageWidth = (context.resources.displayMetrics.widthPixels * widthProc).toInt()
                imageHeight = (imageWidth * 4) / 3
            }
            heightAspect == ASPECT_3_4 -> {
                imageWidth = (context.resources.displayMetrics.widthPixels * widthProc).toInt()
                imageHeight = (imageWidth * 3) / 4
            }
            heightAspect == ASPECT_16_9 -> {
                imageWidth = (context.resources.displayMetrics.widthPixels * widthProc).toInt()
                imageHeight = (imageWidth * 16) / 9
            }
            heightAspect == ASPECT_9_16 -> {
                imageWidth = (context.resources.displayMetrics.widthPixels * widthProc).toInt()
                imageHeight = (imageWidth * 9) / 16
            }
            ////-----> WIDTH ASPECT
            widthAspect == ASPECT_4_3 -> {
                imageHeight = (context.resources.displayMetrics.heightPixels * heightProc).toInt()
                imageWidth = (imageHeight * 4) / 3
            }
            widthAspect == ASPECT_3_4 -> {
                imageHeight = (context.resources.displayMetrics.heightPixels * heightProc).toInt()
                imageWidth = (imageHeight * 3) / 4
            }
            widthAspect == ASPECT_16_9 -> {
                imageHeight = (context.resources.displayMetrics.heightPixels * heightProc).toInt()
                imageWidth = (imageHeight * 16) / 9
            }
            widthAspect == ASPECT_9_16 -> {
                imageHeight = (context.resources.displayMetrics.heightPixels * heightProc).toInt()
                imageWidth = (imageHeight * 9) / 16
            }
            else -> {
                imageWidth = widthMeasureSpec
                imageHeight = heightMeasureSpec
            }
        }

        setMeasuredDimension(imageWidth, imageHeight)
    }

    companion object {
        private const val ASPECT_4_3 = "0"
        private const val ASPECT_3_4 = "1"
        private const val ASPECT_16_9 = "2"
        private const val ASPECT_9_16 = "3"
    }
}