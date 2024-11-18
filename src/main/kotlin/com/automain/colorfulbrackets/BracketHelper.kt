package com.automain.colorfulbrackets

import com.automain.colorfulbrackets.type.BracketType
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.Language
import com.intellij.lang.LanguageBraceMatching
import com.intellij.lang.LanguageExtension
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.ui.JBColor
import java.awt.Color
import kotlin.random.Random

object BracketHelper {
    private val bracketType = LanguageExtension<BracketType>("com.AutoMain.ColorfulBrackets.bracketType")

    fun needCheck(element: PsiElement): Boolean {
        if (isRightBracket(element)) return false
        return element is LeafPsiElement
    }

    fun findRightBracket(element: PsiElement): PsiElement {
        /*根据给定元素向右查找符合type的element*/
        val languages = Language.getRegisteredLanguages()
        for (language in languages) {
            val pairs = LanguageBraceMatching.INSTANCE.forLanguage(language)?.pairs.let {
                if (it.isNullOrEmpty()) {
                    language.associatedFileType
                        ?.let { fileType ->
                            BraceMatchingUtil.getBraceMatcher(
                                fileType,
                                language
                            ) as? PairedBraceMatcher
                        }
                        ?.pairs
                } else {
                    it
                }
            }
            val pairsList = bracketType.forLanguage(language)?.pairs()?.let {
                if (!pairs.isNullOrEmpty()) {
                    it.toMutableSet().apply { addAll(pairs) }
                } else {
                    it
                }
            } ?: pairs?.toList()
            println(pairsList)
        }

        return element.parent
    }

    private fun isRightBracket(element: PsiElement): Boolean {
        return false
    }

    fun getRandomColor(): JBColor {
        val red = Random.nextInt(50, 256)
        val green = Random.nextInt(50, 256)
        val blue = Random.nextInt(50, 256)
        return JBColor(Color(red, green, blue), Color(red, green, blue))
    }
}