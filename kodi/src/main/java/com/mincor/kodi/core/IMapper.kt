package com.mincor.kodi.core

/**
 * Helper interface for bind generic instances with name
 */
interface IMapper<T : Any> {
    val instanceMap: MutableMap<String, T>
}

inline fun <reified T : Any> IMapper<T>.createOrGet(key: String, inst: () -> T): T = this.instanceMap.getOrPut(key) { inst() }
inline fun <reified T : Any> IMapper<T>.has(key: String):Boolean = this.instanceMap.containsKey(key)
inline fun <reified T : Any> IMapper<T>.removeAll() {
    instanceMap.forEach { _, instance ->
        (instance as? ProviderHolder<T>)?.clear()
    }
    instanceMap.clear()
}
inline fun <reified T : Any> IMapper<T>.remove(key: String) {
    if(this.instanceMap.containsKey(key)) (this.instanceMap.remove(key) as? ProviderHolder<T>)?.clear()
}
