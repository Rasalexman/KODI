package com.rasalexman.kodiksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class KodiAnnotationsVisitor(
    logger: KSPLogger
) : BaseKodiVisitor(logger) {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: String): ModuleBlock {
        return processBindElement(classDeclaration, data)
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: String): ModuleBlock {
        return processBindElement(function, data)
    }
}