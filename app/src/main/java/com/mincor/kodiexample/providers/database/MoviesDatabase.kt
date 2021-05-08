package com.mincor.kodiexample.providers.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.providers.database.converters.FromListOfIntToStringConverter
import com.mincor.kodiexample.providers.database.converters.FromListOfStringsToStringConverter
import com.mincor.kodiexample.providers.database.dao.IGenresDao
import com.mincor.kodiexample.providers.database.dao.IMoviesDao
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle

@Database(entities = [
    GenreEntity::class,
    MovieEntity::class
], version = 2)
@TypeConverters(FromListOfIntToStringConverter::class, FromListOfStringsToStringConverter::class)
abstract class MoviesDatabase : RoomDatabase() {

    @BindProvider(
            toClass = IGenresDao::class,
            toModule = Consts.Modules.ProvidersName
    )
    abstract fun getGenresDao(): IGenresDao

    @BindProvider(
            toClass = IMoviesDao::class,
            toModule = Consts.Modules.ProvidersName
    )
    abstract fun getMoviesDao(): IMoviesDao


    companion object {
        @Volatile
        private var INSTANCE: MoviesDatabase? = null

        @BindSingle(
                toClass = MoviesDatabase::class,
                toModule = Consts.Modules.ProvidersName
        )
        fun getDatabase(context: Context): MoviesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        MoviesDatabase::class.java,
                        "kodiMoviesDB"
                )/*.addMigrations(
                        MIGRATION_1_2
                    )*/.fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}