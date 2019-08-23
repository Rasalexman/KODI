package com.mincor.kodireflect.ext

/**
 * Optional value class holder
 */
sealed class Optional<out T> {

    /**
     * Getter the value
     */
    abstract fun get(): T

    /**
     * Some value class
     *
     * @param value
     * there is a some optional value
     */
    data class Some<out T>(private val value: T) : Optional<T>() {
        override fun get() = value
    }

    /**
     * Non value class
     */
    class None<out T> : Optional<T>() {
        override fun get(): T {
            throw NoSuchElementException("Can't get object from Optional.None")
        }
    }
}