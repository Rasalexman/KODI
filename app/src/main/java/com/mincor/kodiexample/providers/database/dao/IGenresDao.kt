package com.mincor.kodiexample.providers.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.mincor.kodiexample.providers.database.dao.base.IBaseDao
import com.mincor.kodiexample.data.model.local.GenreEntity

@Dao
interface IGenresDao : IBaseDao<GenreEntity> {

    @Query("SELECT * FROM GenreEntity ORDER BY name ASC")
    suspend fun getAll(): List<GenreEntity>
}