// Copyright (c) 2018 Aleksandr Minkin (sphc@yandex.ru)
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

package com.mincor.kodi.ext

import com.mincor.kodi.core.Kodi
import com.mincor.kodi.core.single
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Create instance of a given KClass with parameters
 * It used for create an instance of Proxy/Commands/Mediators classes
 *
 * @param values
 * constructor parameters as vararg
 */
inline fun <reified T : Any> KClass<T>.createInstance(values: List<Any>? = null): T {
    val cons = this.primaryConstructor!!
    val valmap = values?.toValMap(cons.parameters) ?: mutableMapOf()
    return cons.callBy(valmap)
}

/**
 * Inject given params to constructor instance members properties
 * All properties must be a KMutableProperty1
 */
fun Any.injectInConstructor(consParams: List<Any>? = null): Any {
    consParams?.let { params ->
        val members = this.javaClass.kotlin.memberProperties as? List<KProperty1<Any, Any>>
        members?.let {
            val ms = it.size
            params.forEachIndexed { index, param ->
                if (ms > index) {
                    val kProperty1 = it[index]
                    val paramName = param.className()
                    val propName = kProperty1.returnType.toString().replace("?", "")
                    if (paramName == propName) {
                        (kProperty1 as? KMutableProperty1<Any, Any>)?.set(this, param)
                    }
                }
            }
        }

    }
    return this
}

/**
 * Helper extension function to convert list of params to valmap params of provider function
 */
fun List<Any>.toValMap(params: List<KParameter>?): MutableMap<KParameter, Any> {
    val valmap = mutableMapOf<KParameter, Any>()
    if (this.isNotEmpty()) {
        params?.forEachIndexed { index, kParameter ->
            valmap[kParameter] = this[index]
        }
    } else {
        val neededParam = params?.firstOrNull { !it.isOptional && it.kind == KParameter.Kind.VALUE }
        neededParam?.let {
            throw RuntimeException("You must set default value to field - '${it.name}', cause its not marked as optional ")
        }
    }
    return valmap
}

/**
 * Ext function
 */
fun Any.className(): String {
    val clazz = this as? KClass<*> ?: this.javaClass.kotlin
    return clazz.qualifiedName ?: clazz.java.name
}
