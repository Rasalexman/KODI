package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.common.Consts.Modules.LDSName
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.providers.database.dao.IGenresDao
import com.rasalexman.kodi.annotations.BindSingle

@BindSingle(
        toClass = IGenresLocalDataSource::class,
        toModule = LDSName
)
class GenresLocalDataSource(
    private val genresDao: IGenresDao
) : IGenresLocalDataSource {
    override suspend fun getGenresList(): SResult<List<GenreEntity>> {
        val allItems = genresDao.getAll()
        return if(allItems.isEmpty()) emptyResult() else successResult(allItems)
    }
    override suspend fun insertGenres(data: List<GenreEntity>) = genresDao.insertAll(data)
}

interface IGenresLocalDataSource {
    suspend fun getGenresList(): SResult<List<GenreEntity>>
    suspend fun insertGenres(data: List<GenreEntity>)
}