package com.rasalexman.kodi.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class BindInstance(
        val toClass: KClass<out Any>,
        val asType: String = "",
        val toTag: String = "",
        val atScope: String = "",
        val toModule: String = ""
)