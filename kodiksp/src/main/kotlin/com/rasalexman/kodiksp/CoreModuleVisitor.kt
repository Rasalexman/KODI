package com.rasalexman.kodiksp

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.CodeBlock

typealias ModuleBlock = Pair<String, CodeBlock>

abstract class CoreModuleVisitor : KSVisitor<String, ModuleBlock?> {

    override fun visitNode(node: KSNode, data: String): ModuleBlock? = null
    override fun visitAnnotated(annotated: KSAnnotated, data: String): ModuleBlock? = null
    override fun visitAnnotation(annotation: KSAnnotation, data: String): ModuleBlock? = null
    override fun visitModifierListOwner(
        modifierListOwner: KSModifierListOwner,
        data: String
    ): ModuleBlock? = null

    override fun visitDeclaration(declaration: KSDeclaration, data: String): ModuleBlock? = null

    override fun visitDeclarationContainer(
        declarationContainer: KSDeclarationContainer,
        data: String
    ): ModuleBlock? = null

    override fun visitDynamicReference(reference: KSDynamicReference, data: String): ModuleBlock? =
        null

    override fun visitFile(file: KSFile, data: String): ModuleBlock? = null

    override fun visitFunctionDeclaration(
        function: KSFunctionDeclaration,
        data: String
    ): ModuleBlock? = null

    override fun visitCallableReference(
        reference: KSCallableReference,
        data: String
    ): ModuleBlock? = null

    override fun visitParenthesizedReference(
        reference: KSParenthesizedReference,
        data: String
    ): ModuleBlock? = null

    override fun visitPropertyDeclaration(
        property: KSPropertyDeclaration,
        data: String
    ): ModuleBlock? = null

    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: String): ModuleBlock? =
        null

    override fun visitPropertyGetter(getter: KSPropertyGetter, data: String): ModuleBlock? = null

    override fun visitPropertySetter(setter: KSPropertySetter, data: String): ModuleBlock? = null

    override fun visitClassifierReference(
        reference: KSClassifierReference,
        data: String
    ): ModuleBlock? = null

    override fun visitReferenceElement(element: KSReferenceElement, data: String): ModuleBlock? =
        null

    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: String): ModuleBlock? = null

    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: String): ModuleBlock? = null

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: String
    ): ModuleBlock? = null

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: String): ModuleBlock? =
        null

    override fun visitTypeReference(typeReference: KSTypeReference, data: String): ModuleBlock? =
        null

    override fun visitValueParameter(valueParameter: KSValueParameter, data: String): ModuleBlock? =
        null

    override fun visitValueArgument(valueArgument: KSValueArgument, data: String): ModuleBlock? =
        null
}