package com.rasalexman.kodi.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class BindSingle(
        val toClass: KClass<out Any>,
        val toTag: String = "",
        val atScope: String = "",
        val toModule: String = ""
)