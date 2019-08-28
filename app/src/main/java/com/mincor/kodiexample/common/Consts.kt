package com.mincor.kodiexample.common

import java.text.SimpleDateFormat
import java.util.*

object Consts {

    val UI_DATE_FORMATTER
        get() = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    val MODEL_DATE_FORMATTER
            get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val currentDate: String
        get() = MODEL_DATE_FORMATTER.format(Date()).toString()

    // PAGES CONST
    const val PAGES_DEFAULT_MAX_COUNT = 1000
}