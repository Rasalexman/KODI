package com.mincor.kodiexample.modules

import com.mincor.kodiexample.presentation.genres.GenresContract
import com.mincor.kodiexample.presentation.genres.GenresPresenter
import com.mincor.kodiexample.presentation.movies.MoviesContract
import com.mincor.kodiexample.presentation.movies.MoviesPresenter
import com.rasalexman.kodi.core.*

val presentationModule = kodiModule {
    bind<GenresContract.IPresenter>() with single { GenresPresenter(instance()) }
    bind<MoviesContract.IPresenter>() with single { MoviesPresenter(instance()) }
}