package com.rasalexman.kodi.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class BindProvider(
        val toClass: KClass<out Any>,
        val toTag: String = "",
        val atScope: String = "",
        val toModule: String = ""
)