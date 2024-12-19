package com.automain.colorfulbrackets

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class ColorfulBracketsAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!BracketHelper.pluginIsOpen()) return

        val pairKey = BracketHelper.getBracketSettingKey(element) ?: return
        if (!PluginSetting.getBracketSetting(pairKey)) return

        val depth = BracketHelper.getBracketDepth(element)
        val attrKey = ColorHelper.getTextAttributesKey(depth)

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(attrKey)
            .create()
    }
}
