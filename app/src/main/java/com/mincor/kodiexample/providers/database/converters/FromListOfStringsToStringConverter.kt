package com.mincor.kodiexample.providers.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class FromListOfStringsToStringConverter {
    @TypeConverter
    fun fromStringList(listOfStrings: List<String>): String {
        return listOfStrings.joinToString(",")
    }

    @TypeConverter
    fun toStringList(data: String): MutableList<String>? {
        return data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
    }
}