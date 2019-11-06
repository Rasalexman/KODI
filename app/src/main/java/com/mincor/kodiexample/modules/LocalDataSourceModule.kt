package com.mincor.kodiexample.modules

import com.mincor.kodi.core.*
import com.mincor.kodiexample.data.source.local.*

val localDataSourceModule = kodiModule {
    bind<IGenresLocalDataSource>() with single { GenresLocalDataSource(instance()) }
    bind<IGenresCacheDataSource>() with single { GenresCacheDataSource() }
    bind<IMoviesLocalDataSource>() with single { MoviesLocalDataSource(instance()) }
}