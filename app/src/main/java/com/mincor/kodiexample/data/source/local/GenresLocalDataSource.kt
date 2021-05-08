package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.common.Consts.Modules.LDSName
import com.mincor.kodiexample.data.dto.SResult
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
    override suspend fun getGenresList(): SResult.Success<List<GenreEntity>> {
        val allItems = genresDao.getAll()
        return successResult(allItems)
    }
    override suspend fun insertGenres(data: List<GenreEntity>) = genresDao.insertAll(data)
}