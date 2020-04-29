package com.rasalexman.kodi.annotations

import kotlin.reflect.KClass

@Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.TYPE_PARAMETER,
        AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.SOURCE)
annotation class WithInstance(
        val tag: String = "",
        val scope: String = "",
        val with: KClass<out Any> = Any::class
)