/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.rename

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.MethodReferencesSearch
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.idea.search.ideaExtensions.KotlinReferencesSearchOptions
import org.jetbrains.kotlin.idea.search.ideaExtensions.KotlinReferencesSearchParameters
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

// BUNCH 191
abstract class RenameKotlinPsiProcessor : RenameKotlinPsiProcessorCompat() {

    override fun findReferences(
        element: PsiElement,
        searchScope: SearchScope,
        searchInCommentsAndStrings: Boolean
    ): Collection<PsiReference> {
        val searchParameters = KotlinReferencesSearchParameters(
            element,
            searchScope,
            kotlinOptions = KotlinReferencesSearchOptions(searchForComponentConventions = false)
        )
        val references = ReferencesSearch.search(searchParameters).toMutableSet()
        if (element is KtNamedFunction || (element is KtProperty && !element.isLocal) || (element is KtParameter && element.hasValOrVar())) {
            element.toLightMethods().flatMapTo(references) { MethodReferencesSearch.search(it) }
        }
        return references
    }

}
