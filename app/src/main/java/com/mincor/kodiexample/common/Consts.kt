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

    object Scopes {
        const val GENRES = "GENRES"
    }

    object Tags {
        const val GENRE_USE_CASE = "GENRE_USE_CASE"
        const val GENRE_LOCAL_USE_CASE = "GENRE_LOCAL_USE_CASE"
    }

    object Modules {
        const val UCDetailsName = "useCasesDetails"
        const val UCMoviesName = "useCasesMovies"
        const val UCGenresName = "useCasesGenres"
        const val PresentersName = "presenters"
        const val ProvidersName = "providers"
        const val RDSName = "remoteDataSource"
        const val LDSName = "localDataSource"
        const val RepName = "repository"
    }

}