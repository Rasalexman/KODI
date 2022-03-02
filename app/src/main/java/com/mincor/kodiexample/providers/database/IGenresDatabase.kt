package com.mincor.kodiexample.providers.database

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.providers.database.dao.IGenresDao
import com.rasalexman.kodi.annotations.BindProvider

interface IGenresDatabase {

    @BindProvider(
        toClass = IGenresDao::class,
        toModule = Consts.Modules.ProvidersName
    )
    fun getGenresDao(): IGenresDao
}