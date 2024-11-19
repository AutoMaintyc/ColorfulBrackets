﻿package com.automain.colorfulbrackets

import com.automain.colorfulbrackets.type.BracketType
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.*
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import com.intellij.ui.JBColor
import java.awt.Color
import kotlin.random.Random

object BracketHelper {
    private val bracketType = LanguageExtension<BracketType>("com.AutoMain.ColorfulBrackets.bracketType")

    /** 所有语言的所有括号的type,key是语言的str */
    private val bracketTypeMap = mutableMapOf<String, MutableList<BracePair>>()

    /** 所有语言的elementType整理成 map<语言Str，List<elementType>>的结构。存到bracketTypeMap中 */
    fun initBracketTypeMap() {
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

            if (pairsList != null) {
                val bracePairs = mutableListOf<BracePair>()
                for (pairs in pairsList) {
                    bracePairs.add(pairs)
                }
                bracketTypeMap[language.displayName] = bracePairs
            }

            println(pairsList)
        }
    }

    /** @return 是否叶子节点 */
    fun needCheck(element: PsiElement): Boolean {
        return element is LeafPsiElement
    }

    /** @return （null，传入的 不是括号、右括号） */
    fun findRightBracket(element: PsiElement): PsiElement? {
        /*根据给定元素向右查找符合type的element*/
        val bracketType = isLeftBracket(element)
        return when (bracketType) {
            true -> getRightBracket(element)
            false -> null
            null -> null
        }
    }

    /** While，在当前层次（深度、高度），向右查找 */
    private fun getRightBracket(element: PsiElement): PsiElement? {
        var current = element
        while (current != null) {
            if (needCheck(current)) {
                if (isLeftBracket(current) == false) {
                    return current
                }
            }
            current = current.nextSibling
        }
        return null
    }

    /** @return （null, 不是括号）（==true, 左括号）（==false，右括号） */
    private fun isLeftBracket(element: PsiElement): Boolean? {
        for (pair in bracketTypeMap[element.language.displayName]!!) {
            when (element.elementType) {
                pair.leftBraceType -> return true
                pair.rightBraceType -> return false
            }
        }
        return null
    }

    fun getRandomColor(): JBColor {
        val red = Random.nextInt(50, 256)
        val green = Random.nextInt(50, 256)
        val blue = Random.nextInt(50, 256)
        return JBColor(Color(red, green, blue), Color(red, green, blue))
    }
}