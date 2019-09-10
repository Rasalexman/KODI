package com.mincor.kodiexample.modules

import androidx.room.Room
import com.mincor.kodi.core.*
import com.mincor.kodiexample.providers.database.MoviesDatabase
import com.mincor.kodiexample.providers.database.dao.IGenresDao
import com.mincor.kodiexample.providers.database.dao.IMoviesDao

val databaseModule = kodiModule {

    bind<MoviesDatabase>() with single {
        Room.databaseBuilder(
                instance(),
                MoviesDatabase::class.java, "kodiMoviesDB"
        ).build()
    }

    bind<IGenresDao>() with provider { instance<MoviesDatabase>().getGenresDao() }
    bind<IMoviesDao>() with provider { instance<MoviesDatabase>().getMoviesDao() }
}