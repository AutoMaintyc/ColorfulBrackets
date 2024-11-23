package com.automain.colorfulbrackets

import com.automain.colorfulbrackets.type.BracketType
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.lang.*
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType

object BracketHelper {
    /** 不同语言的括号的拓展点 */
    private val bracketType = LanguageExtension<BracketType>("com.AutoMain.ColorfulBrackets.bracketType")

    /** 所有语言的所有括号的type,key是语言的str */
    private val bracketTypeMap = mutableMapOf<String, MutableList<BracePair>>()

    /** 所有语言的elementType整理成 map<语言Str，List<elementType>>的结构。存到bracketTypeMap中 */
    private fun initBracketTypeMap() {
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
                for (value: BracePair in pairsList) {
                    bracePairs.add(value)
                }
                bracketTypeMap[language.displayName] = bracePairs
            }
        }
    }

    /** While，在当前层次（深度、高度），向右查找 */
    private fun getRightBracket(element: PsiElement): PsiElement? {
        var current: PsiElement? = element
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

    /** 初始化 */
    fun initBracketHelper() {
        initBracketTypeMap()
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

    fun pluginIsOpen(): Boolean {
        return (PluginSetting.getBracketSetting("{}") ||
                PluginSetting.getBracketSetting("[]") ||
                PluginSetting.getBracketSetting("()") ||
                PluginSetting.getBracketSetting("{}"))
    }

    /** 获取PSI元素在PSI树的深度 */
    @Suppress("unused")
    fun getElementDepth(element: PsiElement): Int {
        var depth = 0
        var current = element
        while (current.parent != null) {
            depth++
            current = current.parent
        }
        return depth
    }
}

object ColorHelper {
    /** 随机颜色时，池子的大小 */
    private const val COLOR_COUNT = 16

    /** 插件高亮使用的级别和颜色方案 */
    private val pluginHighlightInfo: HighlightInfoType = HighlightInfoType.HighlightInfoTypeImpl(
        HighlightSeverity.INFORMATION,
        DefaultLanguageHighlighterColors.CONSTANT
    )

    /** 随机颜色时用的TextAttributesKey的池子 */
    private val textAttributesKeyList by lazy {
        val list = mutableListOf<TextAttributesKey>()
        for (i in 1..COLOR_COUNT) {
            val keyStr = "COLORFUL_BRACKETS_KEYWORD$i"
            val key = TextAttributesKey.createTextAttributesKey(keyStr)
            list.add(key)
        }
        return@lazy list
    }

    /** 获取高亮信息 */
    fun getHighlightInfo(element: PsiElement): HighlightInfo? {
        val color = textAttributesKeyList.shuffled().take(1)[0]
        return HighlightInfo.newHighlightInfo(pluginHighlightInfo).textAttributes(color).range(element).create()
    }
}

object PluginSetting {
    private val BracketsSetting = mutableMapOf(
        "{}" to true,
        "<>" to true,
        "[]" to true,
        "()" to true
    )

    fun setBracketSetting(key: String, value: Boolean) {
        if (BracketsSetting.containsKey(key)) {
            PropertiesComponent.getInstance().setValue(key, value)
            BracketsSetting[key] = value
        }
    }

    fun getBracketSetting(key: String): Boolean {
        return BracketsSetting[key] ?: false
    }
}