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

package com.rasalexman.kodigen

import com.google.auto.service.AutoService
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle
import com.rasalexman.kodi.annotations.IgnoreInstance
import com.rasalexman.kodi.annotations.WithInstance
import com.rasalexman.kodi.core.throwKodiException
import com.squareup.kotlinpoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@AutoService(Processor::class)
class KodiProcessor : AbstractProcessor() {

    companion object {

        private const val INSTANCE_TYPE_SINGLE = "single"
        private const val INSTANCE_TYPE_PROVIDER = "provider"

        private const val KODI_PACKAGE_PATH = "com.rasalexman.kodi.core"
        private const val KODI_GENERATED_PATH = "com.kodi.generated.modules."

        private const val DEFAULT_MODULE_NAME = "Module"
        private const val KODI_DEFAULT_MODULE_NAME = "kodi"
        private const val KODI_MODULE_PROPERTY_TYPE = "IKodiModule"

        private const val KODI_MEMBER_MODULE = "kodiModule"
        private const val KODI_MEMBER_BIND = "bind"
        private const val KODI_MEMBER_AT = "at"
        private const val KODI_MEMBER_WITH = "with"
        private const val KODI_MEMBER_INSTANCE = "instance"

        private const val TAG_MEMBER_NAME = "%M"
        private const val TAG_PACKAGE_NAME = "%S"
        private const val TAG_CLASS_NAME = "%T"

        private const val KODI_TAG_PATTERN = "tag = %S"
        private const val KODI_SCOPE_PATTERN = "scope = %S"

        private const val KODI_ERROR_PACKAGE_NAME = "java."

        private const val FILE_COMMENT = "This file was generated by KodiProcessor. Do not modify!"

        private const val ERROR_ANNOTATION_WITHOUT_PROPERTY_WITH =
                "You cannot use `@%s` annotation property `toClass: KClass<out Any>` to bind on method %s with java classes. Please specify it with `T::class`"
    }

    /* Processing Environment helpers */
    private var filer: Filer by Delegates.notNull()
    /* message helper */
    private var messager: Messager by Delegates.notNull()

    /** Element Utilities, obtained from the processing environment */
    private var ELEMENT_UTILS: Elements by Delegates.notNull()
    /** Type Utilities, obtained from the processing environment */
    private var TYPE_UTILS: Types by Delegates.notNull()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        ELEMENT_UTILS = processingEnv.elementUtils
        TYPE_UTILS = processingEnv.typeUtils
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
                BindSingle::class.java.canonicalName,
                BindProvider::class.java.canonicalName
        )
    }

    override fun getSupportedOptions(): Set<String?>? {
        return Collections.singleton("org.gradle.annotation.processing.aggregating")
        //return Collections.singleton("org.gradle.annotation.processing.isolating")
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val startTime = System.currentTimeMillis()
        println("KodiAnnotationProcessor started")
        val modulesMap = mutableMapOf<String, MutableList<KodiBindData>>()
        collectAnnotationData(roundEnv.getElementsAnnotatedWith(BindProvider::class.java), modulesMap, ::getDataFromBindProvider)
        collectAnnotationData(roundEnv.getElementsAnnotatedWith(BindSingle::class.java), modulesMap, ::getDataFromBindSingle)
        modulesMap.forEach(::processModules)
        println("KodiAnnotationProcessor finished in `${System.currentTimeMillis() - startTime}` ms")
        return false
    }

    private fun processModules(moduleName: String, moduleElements: List<KodiBindData>) {
        val lowerModuleName = moduleName.apply { this[0].lowercaseChar() }
        val packageName = "$KODI_GENERATED_PATH${moduleName.lowercase(Locale.ENGLISH)}"
        val fileName = "${lowerModuleName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ENGLISH
            ) else it.toString()
        }}$DEFAULT_MODULE_NAME"

        val codeInitializer = buildCodeBlock {
            add("$TAG_MEMBER_NAME {", MemberName(KODI_PACKAGE_PATH, KODI_MEMBER_MODULE))
            moduleElements.forEach { bindingData ->

                val element = bindingData.element
                val toClass = bindingData.toClass
                val toPack = bindingData.toPack
                val instanceType = bindingData.instanceType
                val scope = bindingData.scope
                val tag = bindingData.tag

                if (toClass.contains(KODI_ERROR_PACKAGE_NAME)) {
                    throwKodiException<IllegalStateException>("You cannot use java class $toClass as binding class cause it's goes to unexpected graph.")
                }

                add(" \n")
                add(TAG_MEMBER_NAME, MemberName(KODI_PACKAGE_PATH, KODI_MEMBER_BIND))
                add("<$TAG_CLASS_NAME>", ClassName(toPack, toClass))
                if (tag.isNotEmpty()) {
                    add("($KODI_TAG_PATTERN) ", tag)
                } else {
                    add("() ")
                }

                if (scope.isNotEmpty()) {
                    add("$TAG_MEMBER_NAME $TAG_PACKAGE_NAME ", MemberName(KODI_PACKAGE_PATH, KODI_MEMBER_AT), scope)
                }

                add("$TAG_MEMBER_NAME ", MemberName(KODI_PACKAGE_PATH, KODI_MEMBER_WITH))
                add("$TAG_MEMBER_NAME {", MemberName(KODI_PACKAGE_PATH, instanceType))
                add(" \n")

                if (element.kind == ElementKind.METHOD) {
                    this.bindMethodElement(element)
                } else {
                    this.bindInstanceElement(element)
                }
                add("\n}\n")
            }
            add("}")
        }

        val typeName = ClassName(KODI_PACKAGE_PATH, KODI_MODULE_PROPERTY_TYPE)
        val moduleProperty = PropertySpec
                .builder("$lowerModuleName$DEFAULT_MODULE_NAME", typeName)
                .initializer(codeInitializer).build()

        val file = FileSpec.builder(packageName, fileName)
                .addComment(FILE_COMMENT)
                .addProperty(moduleProperty)
                .build()
        try {
            file.writeTo(filer)
        } catch (e: IOException) {
            val message = java.lang.String.format("Unable to write file: %s", e.message)
            messager.printMessage(Diagnostic.Kind.ERROR, message)
        }
    }

    private fun getDataFromBindSingle(element: Element): KodiBindData {
        val annotation = element.getAnnotation(BindSingle::class.java)
        val toClassPackageAndClass = element.getAnnotationClassValue<BindSingle> { toClass }.toString()
        val (packName, className) = toClassPackageAndClass.getPackAndClass()
        return KodiBindData(
                element = element,
                toModule = annotation.toModule,
                toPack = packName,
                toClass = className,
                instanceType = INSTANCE_TYPE_SINGLE,
                scope = annotation.atScope,
                tag = annotation.toTag
        )
    }

    private fun getDataFromBindProvider(element: Element): KodiBindData {
        val annotation = element.getAnnotation(BindProvider::class.java)
        val packageAndClass = element.getAnnotationClassValue<BindProvider> { toClass }.toString()
        val (packName, className) = packageAndClass.getPackAndClass()
        return KodiBindData(
                element = element,
                toModule = annotation.toModule,
                toPack = packName,
                toClass = className,
                instanceType = INSTANCE_TYPE_PROVIDER,
                scope = annotation.atScope,
                tag = annotation.toTag
        )
    }

    private fun CodeBlock.Builder.bindInstanceElement(element: Element) {
        val packageName = ELEMENT_UTILS.getPackageOf(element).toString()
        val className = element.simpleName.toString()

        val isAbstract = element.modifiers.contains(Modifier.ABSTRACT)
        if (isAbstract) {
            addInstance(packageName = packageName, className = className)
        } else {
            add("$TAG_CLASS_NAME(", ClassName(packageName, className))
            val constructor = element.enclosedElements.find { enclosedElement ->
                enclosedElement.kind == ElementKind.CONSTRUCTOR
            } as? ExecutableElement

            var propertiesCount = 0
            constructor?.parameters?.forEach { property ->
                if (addProperty(property = property, count = propertiesCount)) {
                    propertiesCount++
                }
            }
            if (propertiesCount > 0) add("\n")
            add(")")
        }
    }

    private fun CodeBlock.Builder.bindMethodElement(element: Element) {
        val packageOf = ELEMENT_UTILS.getPackageOf(element).toString()
        val parentElement = TYPE_UTILS.asElement(element.enclosingElement.asType())
        val methodName = element.simpleName.toString()
        val parentName = parentElement.simpleName.toString()

        if (packageOf.contains(KODI_ERROR_PACKAGE_NAME)) {
            val error = ERROR_ANNOTATION_WITHOUT_PROPERTY_WITH.format(methodName)
            messager.printMessage(Diagnostic.Kind.ERROR, error)
            throwKodiException<IllegalArgumentException>(error)
        }

        val isAbstract = element.modifiers.contains(Modifier.ABSTRACT) || parentElement.modifiers.contains(Modifier.ABSTRACT)

        when {
            isAbstract -> {
                addInstance(packageName = packageOf, className = parentName)
                add(".$methodName(")
            }
            element.modifiers.contains(Modifier.STATIC) -> {
                add("$TAG_MEMBER_NAME(", MemberName(packageOf, methodName))
            }
            parentElement.modifiers.contains(Modifier.STATIC) -> {
                val parentPackageOf = parentElement.asType().toString()
                val companionElement = TYPE_UTILS.asElement(parentElement.enclosingElement.asType())
                val companionName = companionElement.simpleName.toString()
                val companionPackageOf = ELEMENT_UTILS.getPackageOf(element).toString()
                add("$TAG_CLASS_NAME.$TAG_MEMBER_NAME(", ClassName(companionPackageOf, companionName), MemberName(parentPackageOf, methodName))
            }
            else -> {
                val parentPackageOf = parentElement.asType().toString()
                add("$TAG_CLASS_NAME.$TAG_MEMBER_NAME(", ClassName(packageOf, parentName), MemberName(parentPackageOf, methodName))
            }
        }

        var propCount = 0
        (element as? ExecutableElement)?.parameters?.forEach { variable ->
            if (addProperty(variable, propCount)) {
                propCount++
            }
        }
        if (propCount > 0) add("\n")
        add(")")
    }

    private fun CodeBlock.Builder.addProperty(property: Element, count: Int): Boolean {
        val parameterType = property.javaClass.asTypeName().toString()
        return if (!parameterType.contains(KODI_ERROR_PACKAGE_NAME) && property.getAnnotation(IgnoreInstance::class.java) == null) {
            val propertyName = property.simpleName.toString()
            if (count > 0) {
                add(",")
            }

            add("\n")
            property.getAnnotation(WithInstance::class.java)?.let {
                val tag = it.tag
                val scope = it.scope
                val packageAndClass = property.getAnnotationClassValue<WithInstance> { with }.toString()
                val (packageName, className) = packageAndClass.getPackAndClass()
                add("$propertyName = ")
                addInstance(tag = tag, scope = scope, packageName = packageName, className = className)
            } ?: apply {
                val propertyElement = TYPE_UTILS.asElement(property.asType())
                val propertyPackName = ELEMENT_UTILS.getPackageOf(propertyElement).toString()
                val propertyClassName = propertyElement.simpleName.toString()
                add("$propertyName = ")
                addInstance(packageName = propertyPackName, className = propertyClassName)
            }
            true
        } else false
    }

    private fun CodeBlock.Builder.addInstance(
            tag: String = "",
            scope: String = "",
            packageName: String = "",
            className: String = ""
    ) {
        val instanceMember = MemberName(KODI_PACKAGE_PATH, KODI_MEMBER_INSTANCE)
        add(TAG_MEMBER_NAME, instanceMember)
        if (packageName.isNotEmpty() && className.isNotEmpty() && !packageName.contains(KODI_ERROR_PACKAGE_NAME)) {
            add("<$TAG_CLASS_NAME>", ClassName(packageName, className))
        }

        if (tag.isNotEmpty() && scope.isNotEmpty()) {
            add("($KODI_TAG_PATTERN, $KODI_SCOPE_PATTERN)", instanceMember, tag, scope)
        } else if (tag.isNotEmpty()) {
            add("($KODI_TAG_PATTERN)", instanceMember, tag)
        } else if (scope.isNotEmpty()) {
            add("($KODI_SCOPE_PATTERN)", instanceMember, scope)
        } else {
            add("()")
        }
    }

    private fun collectAnnotationData(
            elementsSet: Set<Element>,
            modulesMap: MutableMap<String, MutableList<KodiBindData>>,
            elementDataHandler: (Element) -> KodiBindData
    ): Boolean {
        elementsSet.forEach { element ->
            val kind = element.kind
            if (kind != ElementKind.METHOD && kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only classes and methods can be annotated as @BindSingle or @BindProvider")
                return true
            }
            val bindingData = elementDataHandler(element)
            val moduleName = bindingData.toModule.takeIf { it.isNotEmpty() }
                    ?: KODI_DEFAULT_MODULE_NAME
            val list = modulesMap.getOrPut(moduleName) { mutableListOf() }
            list.add(bindingData)
        }
        return false
    }

    private fun String.getPackAndClass(): Pair<String, String> {
        val splitted = this.split(".")
        val className = splitted.last()
        val packName = splitted.subList(0, splitted.size - 1).joinToString(".")
        return packName to className
    }

    private inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>) = try {
        getAnnotation(T::class.java).f()
        throw Exception("Expected to get a MirroredTypeException")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    }
}
