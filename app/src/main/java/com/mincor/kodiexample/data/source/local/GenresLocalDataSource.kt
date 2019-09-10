package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.providers.database.dao.IGenresDao

class GenresLocalDataSource(
    private val genresDao: IGenresDao
) : IGenresLocalDataSource {
    override suspend fun getGenresList(): SResult.Success<List<GenreEntity>> = successResult(genresDao.getAll())
    override suspend fun insertGenres(data: List<GenreEntity>) = genresDao.insertAll(data)
}