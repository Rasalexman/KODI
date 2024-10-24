// Copyright (c) 2020 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

@file:Suppress("unused")

package com.rasalexman.kodi.core

/**
 * Base immutable realization provider
 */
interface IImmutableDelegate<T> {
    /**
     * Get the value for property
     * @param thisRef - current reference
     * @param property - property
     */
    operator fun getValue(thisRef: Any?, property: Any): T
}

/**
 * Base mutable realization provider
 */
interface IMutableDelegate<T> : IImmutableDelegate<T> {
    /**
     * Set the value to the property
     * @param thisRef - current reference
     * @param property - property
     * @param value - any generic value
     */
    operator fun setValue(thisRef: Any?, property: Any, value: T)
}

/**
 * Lazy Immutable Delegate initializer wrapper to use withScope `val` withScope keyword `by`. It cannot change his value
 * Example: val someValue by ImmutableDelegate { "Some String" }
 *
 * @param init - func to hold at immutable instance
 */
open class ImmutableDelegate<T>(private val init: InstanceInitializer<T>) : IImmutableDelegate<T> {

    /**
     * Value holder
     */
    protected var value: Optional<T> = Optional.None()

    /**
     * Value getter
     * @param thisRef - current reference
     * @param property - property
     */
    override fun getValue(thisRef: Any?, property: Any): T {
        if (value is Optional.None) {
            value = Optional.Some(Kodi.init())
        }
        return value.get()
    }
}

/**
 * Lazy Mutable Delegate initializer wrapper to use withScope `var` withScope keyword `by`. It can change his value
 * Example: var someValue by ImmutableDelegate { "Some String" }
 *  someValue = "New string to hold"
 *
 * @param init - func to hold at immutable instance
 */
class MutableDelegate<T>(init: InstanceInitializer<T>) : ImmutableDelegate<T>(init),
    IMutableDelegate<T> {
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
        /**
         * Get current value
         */
        override fun get() = value
    }

    /**
     * Non value class
     */
    class None<out T> : Optional<T>() {
        /**
         * Get current value
         */
        @CanThrowException
        override fun get(): T {
            throwKodiException<NoSuchElementException>("Can't get object from Optional.None")
        }
    }
}

/**
 * high order immutable delegate wrapper
 */
inline fun <reified T> immutableGetter(noinline init: InstanceInitializer<T>): IImmutableDelegate<T> =
    ImmutableDelegate(init)

/**
 * high order mutable delegate wrapper
 */
inline fun <reified T> mutableGetter(noinline init: InstanceInitializer<T>): IMutableDelegate<T> =
    MutableDelegate(init)