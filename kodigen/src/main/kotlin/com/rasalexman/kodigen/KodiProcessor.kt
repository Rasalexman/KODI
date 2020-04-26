package com.rasalexman.kodigen

import com.google.auto.service.AutoService
import com.rasalexman.kodi.annotations.*
import com.rasalexman.kodi.core.IKodiModule
import com.rasalexman.kodi.core.KodiHolder
import com.rasalexman.kodi.core.throwKodiException
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.tools.Diagnostic
import kotlin.reflect.KClass

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class KodiAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val KODI_DEFAULT_MODULE_NAME = "Kodi"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(BindSingle::class.java.name, BindProvider::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val startTime = System.currentTimeMillis()
        println("KodiAnnotationProcessor started")
        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val modulesMap = mutableMapOf<String, MutableList<KodiBindData>>()
        collectAnnotationData<BindSingle>(roundEnv, modulesMap, ::takeElementFromBindSingle)
        collectAnnotationData<BindProvider>(roundEnv, modulesMap, ::takeElementFromBindProvider)
        modulesMap.forEach(::processModules)
        println("KodiAnnotationProcessor finished in `${System.currentTimeMillis() - startTime}` ms")
        return false
    }

    private fun processModules(moduleName: String, moduleElements: List<KodiBindData>) {
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return

        val lowerModuleName = moduleName.toLowerCase()
        val packageName = "com.kodi.generated.module.${lowerModuleName}module"
        val fileName = "${lowerModuleName.capitalize()}Module"
        val fileBuilder= FileSpec.builder(packageName, fileName)
                .addImport("com.rasalexman.kodi.core", "bind")
                .addImport("com.rasalexman.kodi.core", "with")
                .addImport("com.rasalexman.kodi.core", "single")
                .addImport("com.rasalexman.kodi.core", "provider")
                .addImport("com.rasalexman.kodi.core", "at")
                .addImport("com.rasalexman.kodi.core", "constant")
                .addImport("com.rasalexman.kodi.core", "instance")
                .addImport("com.rasalexman.kodi.core", "kodiModule")

        val initializer = buildString {
            append("kodiModule {")
            moduleElements.forEach {
                appendln()
                append(processElementAnnotation(it))
                appendln()
            }
            appendln()
            append("}")
        }

        val moduleProperty = PropertySpec.builder("${lowerModuleName}Module", IKodiModule::class)
                .initializer(initializer).build()
        fileBuilder.addProperty(moduleProperty)
        val file = fileBuilder.build()
        val createdFile = File(kaptKotlinGeneratedDir)
        file.writeTo(createdFile)
    }

    private fun processElementAnnotation(bindingData: KodiBindData): String {
        val element = bindingData.element
        val className = element.asType().toString()
        val toClass = bindingData.toClass
        val instanceType = bindingData.instanceType
        val scope = bindingData.scope
        val tag = bindingData.tag

        if(instanceType != KodiHolder.TYPE_SINGLE && instanceType != KodiHolder.TYPE_PROVIDER && instanceType != KodiHolder.TYPE_CONSTANT) {
            throwKodiException<IllegalStateException>("You cannot use this type as annotation processing. Allowed types in KodiHolder.Companion.*")
        }

        val allIgnoredSet = element.enclosedElements.filter { enclosed ->
           enclosed.getAnnotation(IgnoreInstance::class.java) != null
        }.map { it.simpleName.toString() }.toSet()

        return buildString {
            append("bind<$toClass>(")
            if(tag.isNotEmpty()) {
                appendln()
                append("tag = $tag")
                appendln()
            }
            append(") ")
            if(scope.isNotEmpty()) {
                append("at $scope ")
            }
            append("with $instanceType {")
            appendln()
            append("$className(")
            appendln()

            val constructor = element.enclosedElements.find { element ->
                element.kind == ElementKind.CONSTRUCTOR
            } as? ExecutableElement

            var propCount = 0
            constructor?.parameters?.forEach { enclosed ->
                val property = enclosed.simpleName.toString()
                val propertyType = enclosed.asType().toString()
                if (!allIgnoredSet.contains(property)) {
                    if (propCount > 0) appendln()
                    append((if (propCount > 0) "," else "") + "$property = instance<${propertyType}>()")
                    propCount++
                }
            }
            if(propCount > 0) appendln()
            append(")")
            appendln()
            append("}")
        }
    }

    private fun takeElementFromBindSingle(element: Element): KodiBindData {
        val annotation = element.getAnnotation(BindSingle::class.java)
        return KodiBindData(
                element = element,
                toModule = annotation.toModule,
                toClass = element.getAnnotationClassValue<BindSingle> { toClass }.toString(),
                instanceType = KodiHolder.TYPE_SINGLE,
                scope = annotation.atScope.getSecure(),
                tag = annotation.toTag.getSecure()
        )
    }

    private fun takeElementFromBindProvider(element: Element): KodiBindData {
        val annotation = element.getAnnotation(BindProvider::class.java)
        return KodiBindData(
                element = element,
                toModule = annotation.toModule,
                toClass = element.getAnnotationClassValue<BindProvider> { toClass }.toString(),
                instanceType = KodiHolder.TYPE_PROVIDER,
                scope = annotation.atScope.getSecure(),
                tag = annotation.toTag.getSecure()
        )
    }

    private fun String.getSecure(): String {
        return this.takeIf { it.isNotEmpty() }?.run { "\"$this\"" }.orEmpty()
    }

    private inline fun <reified T : Annotation> collectAnnotationData(
            roundEnv: RoundEnvironment,
            modulesMap: MutableMap<String, MutableList<KodiBindData>>,
            elementDataHandler: (Element) -> KodiBindData
    ): Boolean {
        roundEnv.getElementsAnnotatedWith(T::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated as ${T::class.java.simpleName}")
                        return true
                    }
                    val bindingData = elementDataHandler(element)
                    val moduleName = bindingData.toModule.takeIf { it.isNotEmpty() } ?: KODI_DEFAULT_MODULE_NAME
                    val list = modulesMap.getOrPut(moduleName) { mutableListOf() }
                    list.add(bindingData)
                }
        return false
    }

    private inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>) = try {
        getAnnotation(T::class.java).f()
        throw Exception("Expected to get a MirroredTypeException")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    }
}
