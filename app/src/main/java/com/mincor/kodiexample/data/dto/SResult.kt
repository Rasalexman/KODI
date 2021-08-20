package com.mincor.kodiexample.data.dto

import androidx.annotation.Keep

@Keep
sealed class SResult<out T : Any> {

    class Success<out T : Any>(
            val data: T
    ) : SResult<T>()

    object Empty : SResult<Nothing>()
    class Error(val code: Int, val message: String) : SResult<Nothing>()
}

inline fun <reified T : Any> T?.toSuccessResult(): SResult<T> {
    return this?.let { successResult(it) } ?: errorResult(1010, "Result is null")
}
inline fun <reified T : Any> successResult(data: T) = SResult.Success(data)
fun emptyResult() = SResult.Empty
fun errorResult(code: Int, message: String) = SResult.Error(code, message)

inline fun <reified O : Any, reified I : IConvertableTo<O>> SResult<List<I>>.mapListTo(): SResult<List<O>> {
    return when (this) {
        is SResult.Success -> successResult(this.data.mapNotNull { it.convertTo() })
        is SResult.Empty -> this
        is SResult.Error -> this
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfSuccessSuspend(
    crossinline block: suspend (I) -> SResult<O>
): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfEmptySuspend(
    crossinline block: suspend () -> SResult<O>
): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any> SResult<I>.applyIfSuccessSuspend(
    crossinline block: suspend (I) -> Unit
): SResult<I> {
    if (this is SResult.Success) block(this.data)
    return this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any> SResult<I>.applyIfEmptySuspend(
    crossinline block: suspend () -> Unit
): SResult<I> {
    if (this is SResult.Empty) block()
    return this
}

inline fun <reified O : Any, reified I : IConvertableTo<O>> SResult<I>.mapTo(): SResult<O> {
    return when (this) {
        is SResult.Success -> {
            this.data.convertTo()?.let {
                successResult(it)
            } ?: emptyResult()
        }
        is SResult.Empty -> this
        is SResult.Error -> this
    }
}

interface IConvertableTo<T> {
    fun convertTo(): T?
}