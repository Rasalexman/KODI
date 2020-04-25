package com.rasalexman.kodiprocessor

import com.google.auto.service.AutoService
import com.rasalexman.kodi.core.KodiModule
import com.rasalexman.kodiannotation.KodiSingle
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class SingleAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KodiSingle::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        roundEnv.getElementsAnnotatedWith(KodiSingle::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                        return true
                    }
                    processAnnotation(it)
                }
        return false
    }

    private fun processAnnotation(element: Element) {
        val singleAnnotation = element.getAnnotationsByType(KodiSingle::class.java)[0]
        val kodiImport = ClassName("com.rasalexman.kodi.core", "*")
        val kodiModule = ClassName("com.rasalexman.kodi.core", "kodiModule")
        val kodiBind = ClassName("com.rasalexman.kodi.core", "bind")
        val kodiWith = ClassName("com.rasalexman.kodi.core", "with")
        val kodiSingle = ClassName("com.rasalexman.kodi.core", "single")
        val kodiInstance = ClassName("com.rasalexman.kodi.core", "instance")
        val className = element.simpleName.toString()
        val inheritance = singleAnnotation.bindingTo
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "${className}Module"
        val fileBuilder= FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)

        var initializer = "$kodiModule { $kodiBind<$inheritance>() $kodiWith $kodiSingle { $className("

        var classProperty = ""
        var propCount = 0
        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val property = enclosed.simpleName.toString()
                classProperty += (if(propCount > 0) "," else "") + "$property = $kodiInstance()"
                propCount++
            }
        }
        initializer += "$classProperty) }"

        val moduleProperty = PropertySpec.builder("${className.toLowerCase()}Module", KodiModule::class)
                .initializer(initializer).build()
        val companion = TypeSpec.companionObjectBuilder().addProperty(moduleProperty).build()

        val file = fileBuilder.addType(classBuilder.addType(companion).build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val createdFile = File(kaptKotlinGeneratedDir)
        file.writeTo(createdFile)
    }

}
