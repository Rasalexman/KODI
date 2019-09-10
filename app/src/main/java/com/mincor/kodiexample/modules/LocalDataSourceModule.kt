package com.mincor.kodiexample.modules

import com.mincor.kodi.core.bind
import com.mincor.kodi.core.instance
import com.mincor.kodi.core.kodiModule
import com.mincor.kodi.core.single
import com.mincor.kodiexample.data.source.local.*

val localDataSourceModule = kodiModule {
    bind<IGenresLocalDataSource>() with single { GenresLocalDataSource(instance()) }
    bind<IGenresCacheDataSource>() with single { GenresCacheDataSource() }
    bind<IMoviesLocalDataSource>() with single { MoviesLocalDataSource(instance()) }
}