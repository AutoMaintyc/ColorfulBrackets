package com.automain.colorfulbrackets

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import java.util.*

import com.intellij.openapi.editor.markup.*
import java.awt.Color
import java.util.*

object BracketFinder {
    fun findBrackets(project: Project, file: PsiFile, editor: Editor) {
        if (file is PsiFile) {
            val stack = Stack<PsiElement>()
            val pairs = mutableListOf<Pair<PsiElement, PsiElement>>()

            file.accept(object : PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
                    super.visitElement(element)
                    when (element.text) {
                        "{" -> {
                            stack.push(element)
                        }
                        "}" -> {
                            if (stack.isNotEmpty()) {
                                val openingBracket = stack.pop()
                                pairs.add(openingBracket to element)
                            } else {
                                println("Unmatched closing bracket at ${element.textRange}")
                            }
                        }
                    }
                }
            })

            if (stack.isNotEmpty()) {
                println("Unmatched opening brackets:")
                stack.forEach { println("Opening bracket at ${it.textRange}") }
            }

            pairs.forEach { (open, close) ->
                val markupModel = editor.markupModel
                highlightBracket(markupModel, open)
                highlightBracket(markupModel, close)
                println("Found bracket pair: (${open.textRange}, ${close.textRange})")
            }
        }
    }

    private fun highlightBracket(markupModel: MarkupModel, element: PsiElement) {
        val textAttributes = TextAttributes()
        
        textAttributes.foregroundColor = Color.RED // 设置括号颜色为红色
        val highlighter: RangeHighlighter = markupModel.addRangeHighlighter(
            element.textRange.startOffset,
            element.textRange.endOffset,
            HighlighterLayer.ADDITIONAL_SYNTAX,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )
    }
}
