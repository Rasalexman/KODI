package com.mincor.kodiexample.providers.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class FromListOfIntToStringConverter {
    @TypeConverter
    fun fromIntList(listOfInts: List<Int>): String {
        return listOfInts.joinToString(",")
    }

    @TypeConverter
    fun toIntList(data: String): MutableList<Int>? {
        return data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.map { it.toInt() }.toMutableList()
    }
}