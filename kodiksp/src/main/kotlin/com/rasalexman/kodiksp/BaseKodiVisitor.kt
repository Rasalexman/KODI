package com.rasalexman.kodiksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.rasalexman.kodi.core.or
import com.rasalexman.kodi.core.throwKodiException
import com.rasalexman.kodiksp.Consts.KODI_PACKAGE_PATH
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.buildCodeBlock

abstract class BaseKodiVisitor(
    private val logger: KSPLogger
) : CoreModuleVisitor() {

    private companion object {

        private const val KODI_KSP_DEFAULT_MODULE_NAME = "default"
        private const val KODI_MEMBER_BIND = "bind"
        private const val KODI_MEMBER_AT = "at"
        private const val KODI_MEMBER_WITH = "with"
        private const val KODI_MEMBER_INSTANCE = "instance"
        private const val KODI_IGNORE_INSTANCE = "IgnoreInstance"
        private const val KODI_WITH_INSTANCE = "WithInstance"

        private const val TAG_MEMBER_NAME = "%M"
        private const val TAG_PACKAGE_NAME = "%S"
        private const val TAG_CLASS_NAME = "%T"

        private const val PARAM_TO_MODULE = "toModule"
        private const val PARAM_AT_SCOPE = "atScope"
        private const val PARAM_TO_TAG = "toTag"
        private const val PARAM_TO_CLASS = "toClass"

        private const val KODI_TAG = "tag"
        private const val KODI_SCOPE = "scope"
        private const val KODI_TAG_PATTERN = "$KODI_TAG = %S"
        private const val KODI_SCOPE_PATTERN = "$KODI_SCOPE = %S"

        private const val KODI_ERROR_PACKAGE_NAME = "java."

        private const val DOT_DELIMITER = "."

        private const val ERROR_ANNOTATION_WITHOUT_PROPERTY_WITH =
            "You cannot use `@%s` annotation property `toClass: KClass<out Any>` to bind on method %s with java classes. Please specify it with `T::class`"
    }

    protected fun processBindElement(element: KSDeclaration, elementType: String): ModuleBlock {
        val bindingData = getKspDataFromAnnotation(element, elementType)
        val toClass = bindingData.toClass
        if (toClass.contains(KODI_ERROR_PACKAGE_NAME)) {
            throwKodiException<IllegalStateException>(
                message = "You cannot use java class '$toClass' as binding class cause it's goes to unexpected graph."
            )
        }

        verifyBindingTypes(bindingData)

        val toPack = bindingData.toPack
        val toClassName = ClassName(toPack, toClass)

        val innerCodeBlock = buildCodeBlock {
            val instanceType = bindingData.instanceType
            val scope = bindingData.scope
            val tag = bindingData.tag

            add(" \n")
            add(
                TAG_MEMBER_NAME,
                MemberName(
                    KODI_PACKAGE_PATH,
                    KODI_MEMBER_BIND
                )
            )
            add("<$TAG_CLASS_NAME>", toClassName)
            if (tag.isNotEmpty()) {
                add("(${KODI_TAG_PATTERN}) ", tag)
            } else {
                add("() ")
            }

            if (scope.isNotEmpty()) {
                add(
                    "$TAG_MEMBER_NAME $TAG_PACKAGE_NAME ",
                    MemberName(
                        KODI_PACKAGE_PATH, KODI_MEMBER_AT
                    ),
                    scope
                )
            }

            add(
                "$TAG_MEMBER_NAME ",
                MemberName(
                    KODI_PACKAGE_PATH,
                    KODI_MEMBER_WITH
                )
            )
            add(
                "$TAG_MEMBER_NAME {",
                MemberName(KODI_PACKAGE_PATH, instanceType)
            )
            add(" \n")
            if (element is KSClassDeclaration) {
                bindInstanceElement(element = element)
            } else if (element is KSFunctionDeclaration) {
                bindMethodElement(element = element)
            }
            add("\n}\n")
        }
        val moduleName = bindingData.toModule.takeIf { it.isNotEmpty() }
            ?: KODI_KSP_DEFAULT_MODULE_NAME

        return moduleName to innerCodeBlock
    }

    private fun CodeBlock.Builder.bindInstanceElement(element: KSClassDeclaration) {
        val packageName = element.packageName.asString()
        val className = element.simpleName.asString()
        val isAbstract = element.modifiers.contains(Modifier.ABSTRACT)
        if (isAbstract) {
            addInstance(packageName = packageName, className = className)
        } else {
            add("$TAG_CLASS_NAME(", ClassName(packageName, className))
            val constructorParams = element.primaryConstructor?.parameters.orEmpty()
            collectAndCreateProperties(constructorParams)
        }
    }

    private fun CodeBlock.Builder.bindMethodElement(element: KSFunctionDeclaration) {

        val packageName = element.packageName.asString()
        val methodName = element.simpleName.asString()

        /*logger.warn("--> bindMethodElement: ")
        logger.warn("------> packageName = ${packageName} ")
        logger.warn("------> methodName = ${methodName} ")
        logger.warn("------> declarations = ${element.declarations.toList()} ")
        logger.warn("------> modifiers = ${element.modifiers.toList()} ")
        logger.warn("------> functionKind = ${element.functionKind} ")*/

        if (packageName.contains(KODI_ERROR_PACKAGE_NAME)) {
            val error = ERROR_ANNOTATION_WITHOUT_PROPERTY_WITH.format(methodName)
            logger.error(error)
            throwKodiException<IllegalArgumentException>(error)
        }

        val parentElement = element.parentDeclaration.or {
            element.declarations.firstOrNull()
        }
        /*logger.warn("------> parentElement = ${parentElement} ")
        logger.warn("------> parentTypeParameters = ${parentElement?.typeParameters} ")*/
        val parentName = parentElement?.simpleName?.asString().or {
            methodName
        }

        val isAbstract = element.modifiers.contains(Modifier.ABSTRACT) || element.isAbstract
                || parentElement?.modifiers?.contains(Modifier.ABSTRACT) == true
        val isCompanion = parentName == "Companion"
        val isTopLevel = element.functionKind == FunctionKind.TOP_LEVEL

        /*logger.warn("------> parentName = ${parentName} ")
        logger.warn("------> parentModifiers = ${parentElement?.modifiers} ")
        logger.warn("------> isAbstract = ${isAbstract} ")
        logger.warn("------> isCompanion = ${isCompanion} ")*/

        when {
            isAbstract -> {
                addInstance(packageName = packageName, className = parentName)
                add(".$methodName(")
            }
            isTopLevel -> {
                add("$TAG_MEMBER_NAME(", MemberName(packageName, methodName))
            }
            isCompanion -> {
                val parentOfCompanion = parentElement?.run {
                    parentDeclaration ?: this
                }.or { element }
                val companionName = parentOfCompanion.simpleName.asString()
                val companionPackageOf = parentOfCompanion.packageName.asString()
                add("$TAG_CLASS_NAME.$methodName(", ClassName(companionPackageOf, companionName))
            }
            else -> {
                add("$TAG_CLASS_NAME.$methodName(", ClassName(packageName, parentName))
            }
        }

        collectAndCreateProperties(element.parameters)
    }

    private fun CodeBlock.Builder.collectAndCreateProperties(properties: List<KSValueParameter>?) {
        var propCount = 0
        properties?.forEach { variable ->
            if (addProperty(variable, propCount)) {
                propCount++
            }
        }
        if (propCount > 0) add("\n")
        add(")")
    }

    private fun CodeBlock.Builder.addProperty(property: KSValueParameter, count: Int): Boolean {
        val propAnnotations = property.annotations.toList()
        //logger.warn("-----> propAnnotations = ${propAnnotations}")
        val hasDefault = property.hasDefault
        val hasIgnoreAnnotation = propAnnotations.hasAnnotation(name = KODI_IGNORE_INSTANCE)
        if (!hasIgnoreAnnotation && !hasDefault) {
            val propertyName = property.name?.asString().orEmpty()
            if (count > 0) {
                add(",")
            }
            var tag = ""
            var scope = ""

            add("\n")
            add("$propertyName = ")
            val (packageName, className) = propAnnotations.findAnnotation(KODI_WITH_INSTANCE)
                ?.let { withInstance ->
                    val annotationArgs = withInstance.arguments
                    //--- main argument
                    val withType: KSType = annotationArgs.getArgumentValue(KODI_MEMBER_WITH)
                        ?: throwKodiException<KotlinNullPointerException>(
                            message = "Annotation attribute 'with' not set for property '$propertyName'"
                        )
                    //--- additional arguments
                    tag = annotationArgs.getArgumentValue<String>(KODI_TAG).orEmpty()
                    scope = annotationArgs.getArgumentValue<String>(KODI_SCOPE).orEmpty()

                    val classDeclaration = withType.declaration
                    val className = classDeclaration.simpleName.asString()
                    val packName = classDeclaration.packageName.asString()
                    packName to className
                }.or {
                    val propValue = property.type
                    val propDeclaration = propValue.resolve().declaration
                    propDeclaration.packageName.asString() to propDeclaration.simpleName.asString()
                }

            addInstance(tag = tag, scope = scope, packageName = packageName, className = className)
            return true
        }
        return false
    }

    private fun CodeBlock.Builder.addInstance(
        tag: String = "",
        scope: String = "",
        packageName: String = "",
        className: String = ""
    ) {
        val instanceMember = MemberName(KODI_PACKAGE_PATH, KODI_MEMBER_INSTANCE)
        add(TAG_MEMBER_NAME, instanceMember)
        if (packageName.isNotEmpty() && className.isNotEmpty() && !packageName.contains(
                KODI_ERROR_PACKAGE_NAME
            )
        ) {
            add("<$TAG_CLASS_NAME>", ClassName(packageName, className))
        }

        val tagExist = tag.isNotEmpty()
        val scopeExist = scope.isNotEmpty()

        if (tagExist && scopeExist) {
            add("(${KODI_TAG_PATTERN}, ${KODI_SCOPE_PATTERN})", tag, scope)
        } else if (tagExist) {
            add("(${KODI_TAG_PATTERN})", tag)
        } else if (scopeExist) {
            add("(${KODI_SCOPE_PATTERN})", scope)
        } else {
            add("()")
        }
    }

    private fun getKspDataFromAnnotation(element: KSDeclaration, instanceType: String): KspData {
        val firstAnnotation = element.annotations.firstOrNull()
        val annotationArgs: List<KSValueArgument> = firstAnnotation?.arguments.orEmpty()
        val toModule: String = annotationArgs.getArgumentValue<String>(PARAM_TO_MODULE).orEmpty()
        val atScope: String = annotationArgs.getArgumentValue<String>(PARAM_AT_SCOPE).orEmpty()
        val toTag: String = annotationArgs.getArgumentValue<String>(PARAM_TO_TAG).orEmpty()
        val classType: KSType = annotationArgs.getArgumentValue(PARAM_TO_CLASS)
            ?: throwKodiException<KotlinNullPointerException>(
                message = "Annotation attribute 'toClass' not set for '${element.simpleName.asString()}'"
            )
        val classDeclaration = classType.starProjection().declaration

        val (packName, className) = classDeclaration.qualifiedName?.asString()?.getPackAndClass()
            .or {
                classDeclaration.packageName.asString() to classDeclaration.simpleName.asString()
            }
        //logger.warn("-----------------------------")
        //logger.warn("-----> className = ${className} | packName = $packName")
        //logger.warn("-----> toClassType = ${classType}")
        return KspData(
            element = element,
            toModule = toModule,
            toPack = packName,
            toClass = className,
            toClassType = classType,
            instanceType = instanceType,
            scope = atScope,
            tag = toTag
        )/*.also {
            logger.warn("$it")
        }*/
    }

    private fun verifyBindingTypes(bindingData: KspData) {
        val element = bindingData.element

        val elementType: KSType? = when (element) {
            is KSClassDeclaration -> {
                element.asStarProjectedType()
            }
            is KSFunctionDeclaration -> {
                val returnType = element.returnType?.resolve()?.starProjection()
                if (returnType == null) {
                    logger.warn(
                        message = "Failed to resolve return type of function $element, skipping verification",
                        symbol = element
                    )
                }
                returnType
            }
            else -> {
                logger.warn(
                    message = "$element declaration type verification is not supported",
                    symbol = element
                )
                null
            }
        }

        elementType?.let {
            val toClassType = bindingData.toClassType
            val isAssignable = toClassType.isAssignableFrom(elementType)
            if (!isAssignable) {
                logger.error(
                    message = "Incorrect binding at $element \r\n\r\n" +
                            "$elementType is not assignable to $toClassType \r\n",
                    symbol = element
                )
            }
        }
    }

    private fun String.getPackAndClass(): Pair<String, String> {
        val splitted = this.split(DOT_DELIMITER)
        val className = splitted.last()
        val packName = splitted.subList(0, splitted.size - 1).joinToString(DOT_DELIMITER)
        return packName to className
    }

    private fun List<KSAnnotation>.hasAnnotation(name: String): Boolean {
        return findAnnotation(name) != null
    }

    private fun List<KSAnnotation>.findAnnotation(name: String): KSAnnotation? {
        return find { it.annotationType.resolve().declaration.simpleName.asString() == name }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> List<KSValueArgument>.getArgumentValue(name: String): T? {
        return firstOrNull { it.name?.asString() == name }?.value as? T
    }
}