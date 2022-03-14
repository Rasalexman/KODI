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
import com.rasalexman.kodi.annotations.BindSingle

@Database(entities = [
    GenreEntity::class,
    MovieEntity::class
], version = 3, exportSchema = false)
@TypeConverters(FromListOfIntToStringConverter::class, FromListOfStringsToStringConverter::class)
abstract class MoviesDatabase : RoomDatabase(), IBaseDatabase {

    companion object {
        @Volatile
        private var INSTANCE: MoviesDatabase? = null

        @BindSingle(toClass = IGenresDatabase::class, toModule = Consts.Modules.ProvidersName)
        fun provideGenresDatabase(instance: MoviesDatabase): IGenresDatabase = instance

        @BindSingle(toClass = IMoviesDatabase::class, toModule = Consts.Modules.ProvidersName)
        fun provideMoviesDatabase(instance: MoviesDatabase): IMoviesDatabase = instance

        @BindSingle(toClass = IBaseDatabase::class, toModule = Consts.Modules.ProvidersName)
        fun provideBaseDatabase(instance: MoviesDatabase): IBaseDatabase = instance

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
                    )*/.fallbackToDestructiveMigration()
                    .addTypeConverter(FromListOfStringsToStringConverter())
                    .addTypeConverter(FromListOfIntToStringConverter())
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}