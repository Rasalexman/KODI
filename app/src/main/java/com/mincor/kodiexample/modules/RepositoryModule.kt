package com.mincor.kodiexample.modules

import com.rasalexman.kodi.core.*
import com.mincor.kodiexample.data.repository.GenresRepository
import com.mincor.kodiexample.data.repository.IGenresRepository
import com.mincor.kodiexample.data.repository.IMoviesRepository
import com.mincor.kodiexample.data.repository.MoviesRepository

val repositoryModule = kodiModule {
    bind<IGenresRepository>() with single { GenresRepository(instance(), instance(), instance()) }
    bind<IMoviesRepository>() with single { MoviesRepository(instance(), instance()) }
}