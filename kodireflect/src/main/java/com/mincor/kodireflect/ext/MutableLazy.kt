// Copyright (c) 2019 Aleksandr Minkin (sphc@yandex.ru)
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

package com.mincor.kodireflect.ext

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Property delegate to allow lazy initialization of a mutable variable.
 *
 * @param init
 * initialized function
 */
class MutableLazy<T>(val init: () -> T) : ReadWriteProperty<Any?, T> {

    /**
     * Optional value holder
     */
    private var value: Optional<T> = Optional.None()

    /**
     * Standard delegation function overriding
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value is Optional.None) {
            value = Optional.Some(init())
        }
        return value.get()
    }

    /**
     * Standard delegation function overriding
     */
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
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
    class Some<out T>(val value: T) : Optional<T>() {
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