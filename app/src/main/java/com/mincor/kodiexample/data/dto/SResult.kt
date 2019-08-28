package com.mincor.kodiexample.data.dto

sealed class SResult<out T : Any> {
    class Success<out T : Any>(
        val data: T
    ) : SResult<T>()

    object Loading : SResult<Nothing>()
    object Empty : SResult<Nothing>()
    class Error(val code: Int, val message: String) : SResult<Nothing>()
}

inline fun <reified T : Any> successResult(data: T) = SResult.Success(data)
fun loading() = SResult.Loading
fun emptyResult() = SResult.Empty
fun errorResult(code: Int, message: String) = SResult.Error(code, message)

inline fun <reified O : Any, reified I : IConvertableTo<O>> SResult<List<I>>.mapListTo(): SResult<List<O>> {
    return when (this) {
        is SResult.Success -> {
            successResult(this.data.mapNotNull { it.convertTo() })
        }
        is SResult.Empty -> this
        is SResult.Loading -> this
        is SResult.Error -> this
    }
}

inline fun <reified O : Any, reified I : IConvertableTo<O>> SResult<I>.mapTo(): SResult<O> {
    return when (this) {
        is SResult.Success -> {
            this.data.convertTo()?.let {
                successResult(it)
            } ?: emptyResult()
        }
        is SResult.Empty -> this
        is SResult.Loading -> this
        is SResult.Error -> this
    }
}

interface IConvertableTo<T> {
    fun convertTo(): T?
}