package com.automain.colorfulbrackets

import com.intellij.openapi.editor.Editor
import java.util.*
import com.intellij.openapi.editor.markup.*
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.JBColor
import com.intellij.openapi.editor.Document
import java.awt.Color
import kotlin.random.Random
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiComment

object BracketFinder {

    fun findBrackets(file: PsiFile, editor: Editor) {

        val stack = Stack<PsiElement>()
        val pairs = mutableListOf<Pair<PsiElement, PsiElement>>()
        val unmatchedOpenings = mutableListOf<PsiElement>()
        val unmatchedClosings = mutableListOf<PsiElement>()
        file.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                if (isInStringOrComment(element)) {
                    return
                }
                when (element.text) {
                    "{" -> {
                        if (!isInStringOrComment(element)) {
                            stack.push(element)
                        }
                    }

                    "}" -> {
                        if (!isInStringOrComment(element)) {
                            if (stack.isNotEmpty()) {
                                val openingBracket = stack.pop()
                                pairs.add(openingBracket to element)
                            } else {
                                unmatchedClosings.add(element)
                                println("Unmatched closing bracket at ${element.textRange}")
                            }
                        }
                    }
                }
            }
        })

        if (stack.isNotEmpty()) {
            println("Unmatched opening brackets:")
            stack.forEach {
                unmatchedOpenings.add(it)
                println("Opening bracket at ${it.textRange}")
            }
        }

        val markupModel = editor.markupModel
        val document = editor.document
        pairs.forEach { (open, close) ->
            val color = getRandomColor()
            highlightBracket(markupModel, open, color, document)
            highlightBracket(markupModel, close, color, document)
            println("Found bracket pair: (${open.textRange}, ${close.textRange})")
        }

        // Highlight unmatched opening brackets
        unmatchedOpenings.forEach {
            highlightBracket(
                markupModel,
                it,
                JBColor(Color.RED, Color.RED),
                document
            ) // You can choose a specific color for unmatched brackets
        }

        // Highlight unmatched closing brackets
        unmatchedClosings.forEach {
            highlightBracket(
                markupModel,
                it,
                JBColor(Color.RED, Color.RED),
                document
            ) // You can choose a specific color for unmatched brackets
        }

    }

    private fun highlightBracket(markupModel: MarkupModel, element: PsiElement, color: Color, document: Document) {
        val textAttributes = TextAttributes()

        textAttributes.foregroundColor = color
        if (element.startOffset >= 0 && element.endOffset <= document.textLength) {
            markupModel.addRangeHighlighter(
                element.textRange.startOffset,
                element.textRange.endOffset,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
            )
        }
    }

    private fun getRandomColor(): JBColor {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)
        return JBColor(Color(red, green, blue), Color(red, green, blue))
    }

    //    fun isInStringOrComment(element: PsiElement): Boolean {
//        return PsiTreeUtil.getParentOfType(element, PsiComment::class.java, true) != null ||
//                PsiTreeUtil.getParentOfType(element, PsiWhiteSpace::class.java, true) != null
//    }
    //返回false的时候就不继续了
    fun isInStringOrComment(element: PsiElement): Boolean {
        // 检查 element 是否在注释中
        if (PsiTreeUtil.getParentOfType(element, PsiComment::class.java) != null) return true

        // 检查 element 是否在字符串字面量或模板中
        val parent = PsiTreeUtil.getParentOfType(element, PsiLiteralValue::class.java)
        if (parent != null) {
            val value = parent.value
            if (value is String) {
                // 检查 element 是否在字符串字面量中
                val textRange = element.textRange
                val parentRange = parent.textRange
                if (textRange.startOffset > parentRange.startOffset + 1 && textRange.endOffset < parentRange.endOffset - 1) {
                    // 检查 element 是否在字符串模板中
                    val text = parent.text
                    val start = textRange.startOffset - parentRange.startOffset - 1
                    val end = textRange.endOffset - parentRange.startOffset - 1
                    if (text.substring(start, end).contains("\${") || text.substring(start, end).contains("}")) {
                        println("check {} in $ successful")
                        return false
                    }
                }
                return false
            }else{
                return true
            }
        }

        // 如果 element 不在注释或字符串中，返回 false
        return false
    }
}
