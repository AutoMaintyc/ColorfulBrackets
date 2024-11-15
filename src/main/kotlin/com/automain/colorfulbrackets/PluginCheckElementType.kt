package com.automain.colorfulbrackets

import com.goide.template.GoTemplateTypes.LDOUBLE_BRACE
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

class PluginCheckElementType {
    public fun checkElementType(element: PsiElement): Boolean {
        return when (element.elementType) {
            LDOUBLE_BRACE -> {
                true
            }

            else -> {
                false
            }
        }
        return false
    }
}