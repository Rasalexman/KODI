package com.mincor.kodiexample.data.model.remote

import com.mincor.kodiexample.data.dto.IConvertableTo
import com.mincor.kodiexample.data.model.local.GenreEntity

data class GenreModel(
    val id: Int?,
    val name: String?
) : IConvertableTo<GenreEntity> {
    override fun convertTo(): GenreEntity? {
        return if(this.id != null && !this.name.isNullOrEmpty()) {
            GenreEntity(this.id, this.name)
        } else null
    }
}