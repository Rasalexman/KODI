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

package com.rasalexman.kodi.annotations

import kotlin.reflect.KClass

/**
 * Tell the annotation processor to bind field or property with corresponding kodi instance
 * @param tag - [String] tag name to get instance with
 * @param scope - [String] scope name to get instance from
 * @param with - specific class to get instance as
 *
 * * "@BindSingle(toClass = IMyClass::class)"
 * `class MyClass(
 *              @WithInstance(
 *                  tag = "myAwesomeTag",
 *                  scope = "MyAwesomeScope",
 *                  with = IDefaultClass::class
 *                  )
 *              myDefaultValue: IDefaultClass
 *          ) : IMyClass`
 *
 */
@Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.TYPE_PARAMETER,
        AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.SOURCE)
public annotation class WithInstance(
        val tag: String = "",
        val scope: String = "",
        val with: KClass<out Any>
)