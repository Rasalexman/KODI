package com.mincor.kodiexample.providers.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.providers.database.converters.FromListOfIntToStringConverter
import com.mincor.kodiexample.providers.database.dao.IGenresDao
import com.mincor.kodiexample.providers.database.dao.IMoviesDao

@Database(entities = [
    GenreEntity::class,
    MovieEntity::class
], version = 1)
@TypeConverters(FromListOfIntToStringConverter::class)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getGenresDao(): IGenresDao
    abstract fun getMoviesDao(): IMoviesDao
}