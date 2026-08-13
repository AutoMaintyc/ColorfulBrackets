package com.automain.colorfulbrackets

import com.automain.colorfulbrackets.type.BracketType
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.lang.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType

object BracketHelper {
    /** 不同语言的括号的拓展点 */
    private val bracketType = LanguageExtension<BracketType>("com.AutoMain.ColorfulBrackets.bracketType")
    private val bracketKeyByText = mapOf(
        "{" to "{}",
        "}" to "{}",
        "[" to "[]",
        "]" to "[]",
        "(" to "()",
        ")" to "()",
        "<" to "<>",
        ">" to "<>"
    )

    fun getBracePair(element: PsiElement): BracePair? {
        val language = element.language
        
        // 1. 尝试从 LanguageBraceMatching 获取
        val pairs = LanguageBraceMatching.INSTANCE.forLanguage(language)?.pairs
        if (pairs != null) {
            val type = element.elementType
            val pair = pairs.find { type == it.leftBraceType || type == it.rightBraceType }
            if (pair != null) return pair
        }
        
        // 2. 尝试从 FileType 关联的 matcher 获取
        val fileType = language.associatedFileType ?: element.containingFile?.fileType
        if (fileType != null) {
            try {
                val matcher = BraceMatchingUtil.getBraceMatcher(fileType, language)
                if (matcher is PairedBraceMatcher) {
                    val type = element.elementType
                    val pair = matcher.pairs.find { type == it.leftBraceType || type == it.rightBraceType }
                    if (pair != null) return pair
                }
            } catch (_: Exception) {
                // 忽略
            }
        }
        
        // 3. 尝试从自定义扩展点获取
        try {
            val customPairs = bracketType.forLanguage(language)?.pairs()
            if (customPairs != null) {
                val type = element.elementType
                val pair = customPairs.find { type == it.leftBraceType || type == it.rightBraceType }
                if (pair != null) return pair
            }
        } catch (_: Exception) {
            // 忽略
        }

        return null
    }

    fun getBracketSettingKey(element: PsiElement): String? {
        if (element !is LeafPsiElement) return null

        getBracketSettingKeyByText(element)?.let { return it }

        val pair = getBracePair(element) ?: return null
        return when (element.elementType) {
            pair.leftBraceType, pair.rightBraceType -> getBracketSettingKeyByPair(pair)
            else -> null
        }
    }

    fun pluginIsOpen(): Boolean {
        return (PluginSetting.getBracketSetting("{}") ||
                PluginSetting.getBracketSetting("[]") ||
                PluginSetting.getBracketSetting("()") ||
                PluginSetting.getBracketSetting("<>"))
    }

    /** 获取PSI元素在PSI树的深度 */
    fun getBracketDepth(element: PsiElement): Int {
        var depth = 0
        var current: PsiElement? = element.parent
        while (current != null && current !is com.intellij.psi.PsiFile) {
            if (isBracketedContainer(current)) {
                depth++
            }
            current = current.parent
        }
        return depth
    }

    private fun getBracketSettingKeyByText(element: PsiElement): String? {
        return bracketKeyByText[element.text]
    }

    private fun getBracketSettingKeyByPair(pair: BracePair): String? {
        return when {
            pair.leftBraceType.toString().contains("LBRACE", ignoreCase = true) ||
                    pair.rightBraceType.toString().contains("RBRACE", ignoreCase = true) -> "{}"
            pair.leftBraceType.toString().contains("LBRACKET", ignoreCase = true) ||
                    pair.rightBraceType.toString().contains("RBRACKET", ignoreCase = true) -> "[]"
            pair.leftBraceType.toString().contains("LPAREN", ignoreCase = true) ||
                    pair.rightBraceType.toString().contains("RPAREN", ignoreCase = true) -> "()"
            pair.leftBraceType.toString().contains("LT", ignoreCase = true) ||
                    pair.rightBraceType.toString().contains("GT", ignoreCase = true) -> "<>"
            else -> null
        }
    }

    private fun isBracketedContainer(element: PsiElement): Boolean {
        if (element is LeafPsiElement) return false
        
        val first = getFirstSignificantChild(element) ?: return false
        val last = getLastSignificantChild(element) ?: return false
        
        val pair = getBracePair(first)
        if (pair != null) {
            return first.elementType == pair.leftBraceType && last.elementType == pair.rightBraceType
        }

        // Fallback to text matching for parentheses not covered by LanguageBraceMatching
        val firstText = first.text
        val lastText = last.text
        return (firstText == "(" && lastText == ")") ||
               (firstText == "{" && lastText == "}") ||
               (firstText == "[" && lastText == "]") ||
               (firstText == "<" && lastText == ">")
    }

    private fun getFirstSignificantChild(element: PsiElement): PsiElement? {
        var child = element.firstChild
        while (child != null && (child is com.intellij.psi.PsiWhiteSpace || child is com.intellij.psi.PsiComment)) {
            child = child.nextSibling
        }
        return child
    }

    private fun getLastSignificantChild(element: PsiElement): PsiElement? {
        var child = element.lastChild
        while (child != null && (child is com.intellij.psi.PsiWhiteSpace || child is com.intellij.psi.PsiComment)) {
            child = child.prevSibling
        }
        return child
    }
}

object ColorHelper {
    /** 随机颜色时，池子的大小 */
    private const val COLOR_COUNT = 16

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

    fun getTextAttributesKey(depth: Int): TextAttributesKey {
        // 确保深度是非负数
        val safeDepth = if (depth < 0) 0 else depth
        val index = safeDepth % COLOR_COUNT
        return textAttributesKeyList[index]
    }
}

object PluginSetting {
    /** 这里的 map 仅作为缓存，实际值以 PropertiesComponent 为准 */
    private val BracketsSetting = mutableMapOf<String, Boolean>()

    /** 获取括号的开关设置 */
    fun getBracketSetting(key: String): Boolean {
        val pc = PropertiesComponent.getInstance()
        // 优先从缓存取
        BracketsSetting[key]?.let { return it }
        
        // 如果不存在则默认为 true
        if (!pc.isValueSet(key)) {
            pc.setValue(key, true)
            BracketsSetting[key] = true
            return true
        }
        val value = pc.getBoolean(key, true)
        BracketsSetting[key] = value
        return value
    }

    /** 设置括号的开关设置 */
    fun setBracketSetting(key: String, value: Boolean) {
        PropertiesComponent.getInstance().setValue(key, value)
        BracketsSetting[key] = value
    }
}