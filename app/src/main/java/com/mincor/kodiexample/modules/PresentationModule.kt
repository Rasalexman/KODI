package com.mincor.kodiexample.modules

import com.mincor.kodi.core.*
import com.mincor.kodiexample.domain.usecases.genres.GetGenresUseCase
import com.mincor.kodiexample.presentation.genres.GenresContract
import com.mincor.kodiexample.presentation.genres.GenresPresenter
import com.mincor.kodiexample.presentation.movies.MoviesContract
import com.mincor.kodiexample.presentation.movies.MoviesPresenter

val presentationModule = kodiModule {
    bind<GenresContract.IPresenter>() with single { GenresPresenter(instance<GetGenresUseCase>()) }
    bind<MoviesContract.IPresenter>() with single { MoviesPresenter(instance()) }
}