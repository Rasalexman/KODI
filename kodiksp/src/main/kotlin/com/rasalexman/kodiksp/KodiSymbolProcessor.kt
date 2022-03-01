package com.rasalexman.kodiksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle

class KodiSymbolProcessor (
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    //private val verify = environment.options["autoserviceKsp.verify"]?.toBoolean() == true
    //private val verbose = environment.options["autoserviceKsp.verbose"]?.toBoolean() == true

    override fun process(resolver: Resolver): List<KSAnnotated> {

        logger.warn("----> KodiSymbolProcessor start")
        val startTime = System.currentTimeMillis()

        //----- Bind Single Processing
        val singleSymbols = resolver
            // Getting all symbols that are annotated with @Function.
            .getSymbolsWithAnnotation(BIND_SINGLE_NAME)
            // Making sure we take only class declarations.
            .filterIsInstance<KSClassDeclaration>()

        // Exit from the processor in case nothing is annotated with @Function.
        if (!singleSymbols.iterator().hasNext()) return emptyList()

        processSingleAnnotation(singleSymbols)

        logger.warn("----> KodiSymbolProcessor finished in `${System.currentTimeMillis() - startTime}` ms")

        return emptyList()
    }

    private fun processSingleAnnotation(symbols: Sequence<KSClassDeclaration>) {

        symbols.forEach { clz ->
            logger.warn("-----> class = ${clz}")
            val annots = clz.annotations.toList()
            annots.forEach { anno ->
                logger.warn("-----> anno = ${anno.arguments}")
            }
        }
    }

    private companion object {
        val BIND_SINGLE_NAME = BindSingle::class.qualifiedName!!
        val BIND_PROVIDER_NAME = BindProvider::class.qualifiedName!!
    }
}