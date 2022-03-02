package com.mincor.kodiexample.providers.database

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.providers.database.dao.IMoviesDao
import com.rasalexman.kodi.annotations.BindProvider

interface IMoviesDatabase {

    @BindProvider(
        toClass = IMoviesDao::class,
        toModule = Consts.Modules.ProvidersName
    )
    fun getMoviesDao(): IMoviesDao
}