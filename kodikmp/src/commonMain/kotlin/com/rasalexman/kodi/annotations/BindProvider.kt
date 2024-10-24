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
 * This is used for annotation processing with `kodigen` module
 * Can be used with CLASSES, INTERFACES or FUNCTIONS
 *
 * Example:
 * ```
 * interface IAnotherClass
 * interface IMyClass : IAnotherClass
 *
 * @BindProvider(toClass = IAnotherClass::class)
 * class MyClass() : IMyClass
 * ```
 * It is generate `Providing` method for binding like:
 * `bind<IAnotherClass>(tag = [toTag]) at [atScope] with provider { MyClass() }`
 *
 *```
 * object CustomObject {
 *      @BindProvider(toClass = IAnotherClass::class)
 *      fun providingMethod(input: IMyClass): IAnotherClass {
 *          return input
 *      }
 * }
 *```
 * It is generate `Providing` method for binding like:
 * `bind<IAnotherClass>(tag = [toTag]) at [atScope] with provider { CustomObject.providingMethod(input = instance<IMyClass>()) }`
 *
 * for interfaces and abstract classes it always will generate `instance<T>()`:
 * ```
 * @BindProvider(toClass = IMyClass::class)
 * interface IAbstractInterface : IMyClass
 * ```
 * `bind<IMyClass>(tag = [toTag]) at [atScope] with provider { instance<IAbstractInterface>() }`
 *
 * for abstract functions processor always generate instance<T>().`funName()` like:
 * ```
 * interface IAbstractInterface {
 *      @BindProvider(toClass = IAnotherClass::class)
 *      fun getMyAbstractInstance(input: IAbstractInterface): IMyClass
 * }
 * ```
 *`bind<IAnotherClass>(tag = [toTag]) at [atScope] with provider {
 *          instance<IAbstractInterface>().getMyAbstractInstance(
 *                  input = instance<IAbstractInterface>()
 *                  )
 *          }`
 *
 *
 * @param toClass - for what class it should be binded in block `bind<[toClass]>`
 * @param toTag - to which tag it should be binded in block `bind<[toClass]>(tag = [toTag])
 * @param atScope - to which tag it should be binded in block `bind<[toClass]>() at [atScope]
 * @param toModule - custom module name. Default: "Kodi". Processor will generate package and local module `val` with this name
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class BindProvider(
        val toClass: KClass<out Any>,
        val toTag: String = "",
        val atScope: String = "",
        val toModule: String = ""
)