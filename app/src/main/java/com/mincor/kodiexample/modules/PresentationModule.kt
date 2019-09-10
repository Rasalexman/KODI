package com.mincor.kodiexample.modules

import com.mincor.kodi.core.bind
import com.mincor.kodi.core.instance
import com.mincor.kodi.core.kodiModule
import com.mincor.kodi.core.single
import com.mincor.kodiexample.presentation.genres.GenresContract
import com.mincor.kodiexample.presentation.genres.GenresDelegate
import com.mincor.kodiexample.presentation.genres.GenresPresenter
import com.mincor.kodiexample.presentation.movies.MoviesContract
import com.mincor.kodiexample.presentation.movies.MoviesDelegate
import com.mincor.kodiexample.presentation.movies.MoviesPresenter

val presentationModule = kodiModule {
    bind<GenresContract.IPresenter>() with single { GenresPresenter(instance()) }
    bind<GenresDelegate>() with single { GenresDelegate(instance()) }

    bind<MoviesContract.IPresenter>() with single { MoviesPresenter(instance()) }
    bind<MoviesDelegate>() with single { MoviesDelegate(instance()) }
}