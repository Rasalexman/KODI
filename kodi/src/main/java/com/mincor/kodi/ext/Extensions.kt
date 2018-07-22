package com.mincor.kodi.ext

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
fun Any.injectInConstructor(consParams:List<Any>? = null):Any {
    this.javaClass.kotlin.memberProperties.forEach { kProperty1 ->
        consParams?.forEach { param ->
            val paramName = param.className()
            val propName = kProperty1.returnType.toString().replace("?","")
            if(paramName == propName) {
                (kProperty1 as? KMutableProperty1<Any, Any>)?.set(this, param)
            }
        }
    }



    return this
}

fun List<Any>.toValMap(params:List<KParameter>?):MutableMap<KParameter, Any> {
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
