package com.rasalexman.kodiksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle

public class KodiSymbolProcessor (
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private companion object {
        private val BIND_SINGLE_NAME = BindSingle::class.qualifiedName!!
        private val BIND_PROVIDER_NAME = BindProvider::class.qualifiedName!!

        private const val INSTANCE_TYPE_SINGLE = "single"
        private const val INSTANCE_TYPE_PROVIDER = "provider"
    }

    private val logger = environment.logger
    private val kodiModuleGenerator = KodiCodeGenerator(environment.codeGenerator, logger)

    private lateinit var intType: KSType

    override fun process(resolver: Resolver): List<KSAnnotated> {
        intType = resolver.builtIns.intType
        val startTime = System.currentTimeMillis()
        logger.warn("----> KodiSymbolProcessor start")

        //----- collect BindSingle annotations
        val singleSymbols = resolver
            // Getting all symbols that are annotated with @BindSingle.
            .getSymbolsWithAnnotation(BIND_SINGLE_NAME)

        //----- collect BindProvider annotations
        val providerSymbols = resolver
            // Getting all symbols that are annotated with @BindProvider.
            .getSymbolsWithAnnotation(BIND_PROVIDER_NAME)

        // Exit from the processor in case nothing is annotated
        if (!singleSymbols.iterator().hasNext() && !providerSymbols.iterator().hasNext()) {
            return emptyList()
        }

        // clear storage
        kodiModuleGenerator.clear()

        // validate already generated files
        val unableToProcessWithSingle = singleSymbols.filterNot { it.validate() }.toList()
        val unableToProcessWithProvider = providerSymbols.filterNot { it.validate() }.toList()

        // create code blocks for BindSingle
        singleSymbols.filter { it.validate() }.mapNotNull {
            it.accept(KodiAnnotationsVisitor(logger), INSTANCE_TYPE_SINGLE)
        }.forEach(kodiModuleGenerator::processModuleBlock)

        // create code blocks for BindProvider
        providerSymbols.filter { it.validate() }.mapNotNull {
            it.accept(KodiAnnotationsVisitor(logger), INSTANCE_TYPE_PROVIDER)
        }.forEach(kodiModuleGenerator::processModuleBlock)

        // generate code files
        kodiModuleGenerator.generateModules()

        logger.warn("----> KodiSymbolProcessor finished in `${System.currentTimeMillis() - startTime}` ms")

        return unableToProcessWithSingle + unableToProcessWithProvider
    }
}