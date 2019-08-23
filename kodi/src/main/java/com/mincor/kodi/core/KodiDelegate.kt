package com.mincor.kodi.core


interface IImmutableDelegate<T> {
    operator fun getValue(thisRef: Any?, property: Any): T
}
interface IMutableDelegate<T> : IImmutableDelegate<T> {
    operator fun setValue(thisRef: Any?, property: Any, value: T)
}

data class ImmutableDelegate<T>(private val init: () -> T) : IImmutableDelegate<T> {
    /**
     * Value holder
     */
    private val value: Optional<T> = Optional.Some(init())

    /**
     * Value getter
     */
    override fun getValue(thisRef: Any?, property: Any): T {
        return value.get()
    }
}

data class MutableDelegate<T>(private val init: () -> T) : IMutableDelegate<T> {
    /**
     * Optional value holder
     */
    private var value: Optional<T> = Optional.None()

    /**
     * Standard delegation function overriding
     */
    override fun getValue(thisRef: Any?, property: Any): T {
        if (value is Optional.None) {
            value = Optional.Some(init())
        }
        return value.get()
    }

    /**
     * Standard delegation function overriding
     */
    override fun setValue(thisRef: Any?, property: Any, value: T) {
        this.value = Optional.Some(value)
    }
}

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

fun <T> immutableGetter(init: () -> T): IImmutableDelegate<T> = ImmutableDelegate(init)
fun <T> mutableGetter(init: () -> T): IMutableDelegate<T> = MutableDelegate(init)