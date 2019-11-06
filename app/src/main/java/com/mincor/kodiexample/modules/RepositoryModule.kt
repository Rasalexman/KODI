package com.mincor.kodiexample.modules

import com.mincor.kodi.core.*
import com.mincor.kodiexample.data.repository.GenresRepository
import com.mincor.kodiexample.data.repository.MoviesRepository

val repositoryModule = kodiModule {
    bind<GenresRepository>() with single { GenresRepository(instance(), instance(), instance()) }
    bind<MoviesRepository>() with single { MoviesRepository(instance(), instance()) }
}